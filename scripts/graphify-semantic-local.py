#!/usr/bin/env python3
"""Semantic extraction DETERMINISTIK — tanpa Gemini/LLM apapun.

Menggantikan tahap "semantic extraction" (LLM) pipeline graphify untuk file
dokumen dengan analisis referensi berbasis aturan. Hasil ditulis ke SEMANTIC
CACHE graphify (hash, lokasi, dan format entry identik — direplikasi dari
graphify/cache.py + ids.py), sehingga `graphify . --update` menemukan cache HIT
dan tidak memanggil LLM sama sekali.

Pipeline:
  1. python3 scripts/graphify-semantic-local.py            # seed semua miss
  2. graphify . --update                                   # 100% offline

Aturan referensi (deterministik, bukan heuristik LLM):
  - endpoint HTTP (GET/POST/...) + path → controller (dari scan @RequestMapping)
  - nama kelas `XxxController` dalam teks → node kelas
  - token permission `ENTITY:ACTION` → PrefPermissionController
  - referensi `ADR-XXXX` → node doc ADR
  - referensi issue `kepegawaian-xxxx` → node konsep issue
  - penyebutan tooling (GitNexus/Graphify/Beads) di AGENTS.md/CLAUDE.md

Node id TIDAK dihitung ulang — di-resolve dari graphify-out/graph.json yang
sudah ada (sumber kebenaran id AST), jadi tidak ada drift id (#1033/#1104).
"""
from __future__ import annotations

import argparse
import importlib.util
import json
import re
import unicodedata
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent

# ---------------------------------------------------------- utilitas id
def normalize_id(s: str) -> str:
    """Replikasi graphify.ids.normalize_id (hanya untuk node konsep BARU)."""
    s = unicodedata.normalize("NFKC", s)
    s = re.sub(r"[^\w]+", "_", s, flags=re.UNICODE)
    s = re.sub(r"_+", "_", s)
    return s.strip("_").casefold()


def _load_seed_helpers():
    """Pakai file_hash + semantic_cache_dir dari graphify-semantic-seed.py."""
    spec = importlib.util.spec_from_file_location(
        "graphify_seed", ROOT / "scripts" / "graphify-semantic-seed.py")
    m = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(m)
    return m.file_hash, m.semantic_cache_dir


# ---------------------------------------------------------- analisis doc
_METHOD = re.compile(r"\b(GET|POST|PUT|PATCH|DELETE|HEAD|OPTIONS)\b\s*`?([a-zA-Z0-9_/{}.-]*)")
_PATH = re.compile(r"(?<![\w:])(/[a-z][a-z0-9_/{}.-]*)")
_CONTROLLER = re.compile(r"\b([A-Z][A-Za-z0-9]+Controller)\b")
_CLASS = re.compile(r"\b([A-Z][A-Za-z0-9]{2,})\b")
_PERMISSION = re.compile(r"\b[A-Z][A-Z0-9]*(?:_[A-Z][A-Z0-9]*)*:[A-Z][A-Z0-9_]*(?:_[A-Z][A-Z0-9]*)*\b")
_ADR = re.compile(r"\bADR[- ]?(\d{3,4})\b")
_ISSUE = re.compile(r"\b(kepegawaian-[a-z0-9]+)\b")
_TOOLING = {"GitNexus": "gitnexus", "Graphify": "graphify", "Beads": "beads"}


def _norm_path(s: str) -> str:
    """Normalisasi path utk pencocokan: {param} -> {} (casefold)."""
    return re.sub(r"\{[^}]*\}", "{}", s).casefold()


def analyze_doc(rel: str, text: str, idx: SymbolIndex) -> dict:
    """Analisis referensi deterministik -> payload semantic cache."""
    edges: list[dict] = []
    seen: set[tuple[str, str]] = set()

    def add(src: str, tgt: str, conf: str = "EXTRACTED", sf: str = rel):
        key = (src, tgt)
        if key in seen or not tgt:
            return
        seen.add(key)
        edges.append({
            "source": src, "target": tgt, "relation": "references",
            "confidence": conf,
            "confidence_score": 1.0 if conf == "EXTRACTED" else 0.7,
            "source_file": sf,
        })

    doc_id = idx.doc_node_id(rel)
    if not doc_id:
        # doc node baru — pipeline akan menambahkannya dari payload nodes
        doc_id = normalize_id(rel.removesuffix(".md"))
        new_doc = {"id": doc_id, "label": rel.rsplit("/", 1)[-1],
                   "file_type": "document", "source_file": rel}
    else:
        new_doc = None

    # 1) endpoint HTTP method+path dan path polos -> controller
    for m in _METHOD.finditer(text):
        token = m.group(2).strip("`").strip()
        if not token or not token.startswith("/"):
            continue
        tgt = idx.controller_for(token)
        if tgt:
            add(doc_id, tgt, "EXTRACTED")
    for m in _PATH.finditer(text):
        token = m.group(1)
        tgt = idx.controller_for(token)
        if tgt:
            add(doc_id, tgt, "EXTRACTED")

    # 2) nama kelas XxxController (EXTRACTED) + simbol kelas lain (INFERRED)
    for m in _CONTROLLER.finditer(text):
        tgt = idx.class_by_label.get(m.group(1).casefold())
        if tgt:
            add(doc_id, tgt, "EXTRACTED")
    for m in _CLASS.finditer(text):
        tgt = idx.class_by_label.get(m.group(1).casefold())
        if tgt and not tgt.startswith("src_main_java_id_perumdamts_kepegawaian_generated"):
            add(doc_id, tgt, "INFERRED")

    # 3) permission token -> PrefPermissionController (sekali per doc)
    if _PERMISSION.search(text):
        tgt = idx.permission_controller_id()
        if tgt:
            add(doc_id, tgt, "INFERRED")

    # 4) ADR-XXXX -> node doc ADR
    for m in set(_ADR.findall(text)):
        tgt = idx.adr_node(m.zfill(4))
        if tgt:
            add(doc_id, tgt, "INFERRED")

    # 5) issue kepegawaian-xxxx -> node konsep issue
    extra_nodes = []
    for m in set(_ISSUE.findall(text)):
        nid = normalize_id(m)
        if not idx.existing_node(nid):
            extra_nodes.append({"id": nid, "label": m, "file_type": "concept",
                                "source_file": rel})
        add(doc_id, nid, "INFERRED")

    # 6) tooling di AGENTS/CLAUDE
    for label, nid in _TOOLING.items():
        if re.search(rf"\b{label}\b", text) and idx.existing_node(nid):
            add(doc_id, nid, "INFERRED")

    nodes = ([new_doc] if new_doc else []) + extra_nodes
    return {"label": rel.rsplit("/", 1)[-1], "nodes": nodes, "edges": edges,
            "hyperedges": []}


# ---------------------------------------------------------- main
def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__,
                                 formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--root", default=str(ROOT))
    ap.add_argument("--dry-run", action="store_true", help="hanya laporkan, jangan tulis")
    ap.add_argument("--show", action="store_true", help="tampilkan analisis per doc")
    ap.add_argument("--force", action="store_true",
                    help="overwrite entry cache yang sudah ada (re-seed setelah ubah aturan)")
    args = ap.parse_args()

    root = Path(args.root).resolve()
    file_hash, cache_dir_fn = _load_seed_helpers()
    cache_dir = cache_dir_fn(root)

    # indeks simbol — dibangun dua tahap: scan endpoint + resolve id kelas
    g = json.loads((root / "graphify-out" / "graph.json").read_text())
    class_by_label: dict[str, str] = {}
    by_source: dict[str, list[dict]] = {}
    for n in g["nodes"]:
        sf = n.get("source_file")
        if sf:
            by_source.setdefault(sf, []).append(n)
        # hanya TYPE java top-level (label == stem file): menyingkirkan field,
        # enum value, method, dan node config json/yml yang labelnya kata umum
        if (n.get("file_type") == "code" and sf and sf.endswith(".java")
                and n.get("label") == sf.rsplit("/", 1)[-1].removesuffix(".java")):
            class_by_label.setdefault(n["label"].casefold(), n["id"])

    def doc_node_id(rel: str) -> str | None:
        base = rel.rsplit("/", 1)[-1]
        for n in by_source.get(rel, []):
            if n["label"] == base:
                return n["id"]
        return by_source[rel][0]["id"] if by_source.get(rel) else None

    # indeks endpoint: [(normalized_path, controller_class_node_id)] — dibangun sekali
    endpoint_index: list[tuple[str, str]] = []
    ctrl_dir = root / "src/main/java/id/perumdamts/kepegawaian/controllers"
    for java in sorted(ctrl_dir.rglob("*Controller.java")):
        text = java.read_text(errors="replace")
        stem_id = class_by_label.get(java.stem.casefold())
        if not stem_id:
            continue
        cls_map = re.search(r"@RequestMapping\(\s*\"([^\"]*)\"\s*\)", text)
        base = cls_map.group(1) if cls_map else ""
        if base and not base.startswith("/"):
            base = "/" + base
        if base:
            endpoint_index.append((_norm_path(base), stem_id))
        for mm in re.finditer(r"@(?:Get|Post|Put|Patch|Delete|Request)Mapping\(\s*\"([^\"]*)\"\s*\)", text):
            p = mm.group(1)
            if not p.startswith("/"):
                p = "/" + p
            endpoint_index.append((_norm_path((base + p) if base else p), stem_id))

    def controller_for(path_token: str) -> str | None:
        nt = _norm_path(path_token)
        # exact match path penuh dulu
        for full, cid in endpoint_index:
            if full == nt:
                return cid
        # lalu prefix terpanjang
        best: tuple[int, str] | None = None
        for full, cid in endpoint_index:
            if nt == full or nt.startswith(full + "/"):
                if best is None or len(full) > best[0]:
                    best = (len(full), cid)
        return best[1] if best else None

    def adr_node(number: str) -> str | None:
        pat = f"docs/adr/{number}-"
        for sf in by_source:
            if sf.startswith(pat):
                return doc_node_id(sf)
        return None

    class Ctx:
        def __init__(self):
            self.doc_node_id = doc_node_id
            self.controller_for = controller_for
            self.class_by_label = class_by_label
            self.adr_node = adr_node
            self.all_ids = {n["id"] for n in g["nodes"]}

    idx = Ctx()
    idx.existing_node = lambda nid: nid in idx.all_ids
    idx.permission_controller_id = lambda: class_by_label.get("prefpermissioncontroller")

    # korpus doc (sama dgn seed script)
    misses = []
    for p in sorted(root.rglob("*.md")):
        try:
            rel = p.relative_to(root).as_posix()
        except ValueError:
            continue
        if rel.startswith(("graphify-out", ".")) or rel.startswith("docs/draft"):
            continue
        h = file_hash(p, root)
        if args.force or not (cache_dir / f"{h}.json").exists():
            misses.append((rel, p, h))

    print(f"cache dir : {cache_dir}")
    print(f"MISS      : {len(misses)} doc")
    for rel, p, h in misses:
        text = p.read_text(errors="replace")
        analysis = analyze_doc(rel, text, idx)
        if args.show:
            print(f"\n=== {rel} ({len(analysis['edges'])} edges)")
            for e in analysis["edges"]:
                print(f"   {e['confidence'][:4]} {e['target']}")
        if not args.dry_run:
            payload = {"nodes": analysis["nodes"], "edges": analysis["edges"],
                       "hyperedges": analysis["hyperedges"]}
            (cache_dir / f"{h}.json").write_text(
                json.dumps(payload, ensure_ascii=False, indent=1))
            print(f"  seeded {rel} -> {h[:12]}.json ({len(payload['nodes'])} nodes, "
                  f"{len(payload['edges'])} edges)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
