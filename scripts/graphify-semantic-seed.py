#!/usr/bin/env python3
"""Seed semantic cache graphify tanpa LLM — check / seed / verify.

Mengapa ada: langkah "semantic extraction" di pipeline graphify default-nya
memanggil LLM (Gemini). Saat kuota habis (429), `graphify . --update` gagal
menimpa graph. Solusinya: isi SEMANTIC CACHE sendiri — graphify membaca cache
ini dan sama sekali tidak memanggil LLM untuk file yang HIT.

Script ini mereplikasi mekanisme cache graphify secara persis (hash, lokasi,
format entry, id node) sehingga entry yang ditulis langsung dibaca graphify —
tanpa import library graphify dan tanpa LLM.

Pipeline penggunaan:
  1. python3 scripts/graphify-semantic-seed.py --check
     → daftar file yang MISS cache (yang perlu dianalisis)
  2. Baca file tsb, tulis analisis JSON (lihat format di bawah)
  3. python3 scripts/graphify-semantic-seed.py --seed <file> <analysis.json>
     → entry ditulis ke cache dengan hash yang benar
  4. Ulangi 2-3 untuk semua file miss
  5. python3 scripts/graphify-semantic-seed.py --verify
     → pastikan 0 miss
  6. graphify . --update        # full pipeline, 100% cache hit, tanpa LLM
     graphify update .          # (opsional) AST-only rebuild
     GRAPHIFY_VIZ_NODE_LIMIT=20000 graphify cluster-only .   # report + html

Format analysis.json (semua field opsional kecuali nodes/edges kosong boleh):
{
  "label": "Deskripsi singkat dokumen ini",   # label node document
  "nodes": [                                   # konsep yang dirujuk doc
    {"id": "konsep_xyz", "label": "...", "file_type": "concept"}
  ],
  "edges": [                                   # relasi doc -> node lain
    {"source": "<id-node>", "target": "<id-node>", "relation": "implements|configures|uses_multiset|..."}
  ]
}

Aturan yang ditangani script:
- Node document utama (id = normalize_id(rel_path)) dibuat otomatis.
- source_file di-inject otomatis ke SEMUA node/edge (dibutuhkan graphify).
- id source/target dinormalisasi (casefold, non-word -> _) agar cocok dengan graph.
- Node yang id-nya belum ada di graph (node baru) tetap valid — graphify menambahkannya.

Cache & hash yang direplikasi (graphify/cache.py + ids.py):
- salt   = rel_path.lower() (path relatif terhadap root)
- content= body tanpa YAML frontmatter utk .md; raw bytes utk lainnya
- hash   = sha256(content + b"\\x00" + salt)
- lokasi = graphify-out/cache/semantic/pf{fingerprint}/{hash}.json
- pf dir dipilih otomatis: subdir pf* terbaru yang ada (fallback flat semantic/)
"""

import argparse
import fnmatch
import hashlib
import json
import re
import sys
import unicodedata
from pathlib import Path

# Ekstensi yang masuk jalur semantic graphify (detect.DOC_EXTENSIONS)
DOC_EXTS = {".md", ".mdx", ".qmd", ".skill", ".txt", ".rst", ".html", ".yaml", ".yml"}
_FRONTMATTER = re.compile(r"^---[ \t]*\r?$", re.MULTILINE)


# ---------------------------------------------------------------- id (graphify.ids)
def normalize_id(s: str) -> str:
    """Replikasi graphify.ids.normalize_id — id node harus identik."""
    s = unicodedata.normalize("NFKC", s)
    s = re.sub(r"[^\w]+", "_", s, flags=re.UNICODE)
    s = re.sub(r"_+", "_", s)
    return s.strip("_").casefold()


# ---------------------------------------------------------------- hash (graphify.cache)
def body_content(raw: bytes) -> bytes:
    """Untuk .md: buang YAML frontmatter sebelum di-hash (persis graphify)."""
    text = raw.decode(errors="replace")
    opener = _FRONTMATTER.match(text)
    if opener is None:
        return raw
    closer = _FRONTMATTER.search(text, opener.end())
    if closer is None:
        return raw
    return text[closer.start() + 3:].encode()


def file_hash(path: Path, root: Path) -> str:
    """sha256(content + \\x00 + salt) — salt = rel_path lowercase."""
    resolved = path.resolve()
    try:
        salt = resolved.relative_to(root.resolve()).as_posix().lower()
    except ValueError:
        salt = resolved.as_posix().lower()
    raw = resolved.read_bytes()
    content = body_content(raw) if resolved.suffix.lower() == ".md" else raw
    h = hashlib.sha256()
    h.update(content)
    h.update(b"\x00")
    h.update(salt.encode())
    return h.hexdigest()


# ---------------------------------------------------------------- lokasi cache
def semantic_cache_dir(root: Path) -> Path:
    """graphify-out/cache/semantic[/pf{...}] — pilih subdir pf* terbaru."""
    base = root / "graphify-out" / "cache" / "semantic"
    pfs = sorted(base.glob("pf*"), key=lambda p: p.stat().st_mtime_ns) if base.exists() else []
    if len(pfs) > 1:
        print(f"  [warn] {len(pfs)} prompt-fingerprint subdir ditemukan; pakai {pfs[-1].name}",
              file=sys.stderr)
    return pfs[-1] if pfs else base


# ---------------------------------------------------------------- korpus docs
def load_ignore_patterns(root: Path) -> list[str]:
    pats: list[str] = []
    for name in (".graphifyignore", ".gitignore"):
        p = root / name
        if p.exists():
            pats.extend(
                ln.strip() for ln in p.read_text().splitlines()
                if ln.strip() and not ln.strip().startswith("#")
            )
    return pats


def is_ignored(rel: str, patterns: list[str]) -> bool:
    """Approksimasi gitignore: ! negasi (re-include), /-anchored, ** — lihat docstring.

    Limitation: hanya glob sederhana; `!` negasi dibalas (jika pola terakhir
    match, `!` membatalkan). Tidak support /** semantics penuh git.
    """
    ignored = False
    for raw in patterns:
        negate = raw.startswith("!")
        pat = raw[1:].rstrip("/") if negate else raw.rstrip("/")
        match = (
            fnmatch.fnmatch(rel, pat)
            or fnmatch.fnmatch(rel, f"**/{pat}")
            or fnmatch.fnmatch(rel, f"{pat}/**")
        )
        if match:
            ignored = not negate  # last-match-wins; ! membatalkan ignore
    return ignored


def corpus_docs(root: Path) -> list[Path]:
    """Semua file docs (ekstensi semantic) yang masuk corpus graphify."""
    patterns = load_ignore_patterns(root)
    out: list[Path] = []
    for p in sorted(root.rglob("*")):
        if not p.is_file() or p.suffix.lower() not in DOC_EXTS:
            continue
        parts = p.relative_to(root).parts
        if any(part.startswith(".") for part in parts):
            continue  # dot-dir (bawaan graphify skip)
        rel = p.relative_to(root).as_posix()
        if is_ignored(rel, patterns):
            continue
        out.append(p)
    return out


def check_misses(root: Path) -> tuple[list[Path], Path]:
    cache_dir = semantic_cache_dir(root)
    misses: list[Path] = []
    for p in corpus_docs(root):
        h = file_hash(p, root)
        if not (cache_dir / f"{h}.json").exists():
            misses.append(p)
    return misses, cache_dir


# ---------------------------------------------------------------- seed
def seed_file(root: Path, target: Path, analysis: dict, dry_run: bool = False) -> Path:
    """Tulis entry semantic cache untuk target dari analisis JSON."""
    cache_dir = semantic_cache_dir(root)
    rel = target.relative_to(root).as_posix()
    doc_id = normalize_id(rel)

    nodes = list(analysis.get("nodes", []))
    # Node document utama — selalu ada
    if not any(n.get("id") == doc_id for n in nodes):
        nodes.insert(0, {
            "id": doc_id,
            "label": analysis.get("label") or f"{rel} — semantic analysis",
            "file_type": "document",
        })

    # inject source_file ke semua node/edge (wajib graphify)
    for n in nodes:
        n["id"] = normalize_id(str(n["id"]))
        n.setdefault("source_file", rel)

    edges = []
    for e in analysis.get("edges", []):
        edges.append({
            "source": normalize_id(str(e["source"])),
            "target": normalize_id(str(e["target"])),
            "relation": str(e["relation"]),
            "confidence": "EXTRACTED",
            "confidence_score": 1.0,
            "source_file": rel,
        })

    payload = {"nodes": nodes, "edges": edges, "hyperedges": analysis.get("hyperedges", [])}
    h = file_hash(target, root)
    out = cache_dir / f"{h}.json"
    if dry_run:
        print(f"  [dry-run] {rel} -> {out.name} ({len(nodes)} nodes, {len(edges)} edges)")
        return out
    if out.exists():
        print(f"  [warn] entry sudah ada, OVERWRITE: {out.name} — jalankan --dry-run dulu utk cek hash",
              file=sys.stderr)
    out.write_text(json.dumps(payload, ensure_ascii=False, indent=1))
    print(f"  seeded {rel} -> {out.name} ({len(nodes)} nodes, {len(edges)} edges)")
    return out


# ---------------------------------------------------------------- main
def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__,
                                 formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--root", default=".", help="root proyek (default: cwd)")
    ap.add_argument("--check", action="store_true", help="daftar file docs yang MISS cache semantic")
    ap.add_argument("--verify", action="store_true", help="hitung total HIT/MISS seluruh corpus")
    ap.add_argument("--seed", nargs=2, metavar=("FILE", "ANALYSIS_JSON"),
                    help="tulis entry cache untuk FILE dari ANALISIS_JSON")
    ap.add_argument("--dry-run", action="store_true", help="dengan --seed: hanya hitung, jangan tulis")
    args = ap.parse_args()

    root = Path(args.root).resolve()

    if args.seed:
        target = (root / args.seed[0]).resolve()
        if not target.exists():
            print(f"error: file tidak ada: {target}", file=sys.stderr)
            return 2
        analysis = json.loads(Path(args.seed[1]).read_text())
        seed_file(root, target, analysis, dry_run=args.dry_run)
        return 0

    if args.verify:
        misses, cache_dir = check_misses(root)
        total = len(corpus_docs(root))
        print(f"cache dir : {cache_dir}")
        print(f"corpus    : {total} docs")
        print(f"HIT  : {total - len(misses)}")
        print(f"MISS : {len(misses)}")
        for m in misses:
            print(f"  - {m.relative_to(root).as_posix()}")
        print("catatan: --verify memakai subdir yang sama dgn --seed; validasi sejati = jalankan `graphify . --update`")
        return 1 if misses else 0

    # default: --check
    misses, cache_dir = check_misses(root)
    total = len(corpus_docs(root))
    print(f"cache dir : {cache_dir}")
    print(f"corpus    : {total} docs | HIT {total - len(misses)} | MISS {len(misses)}")
    for m in misses:
        print(f"  - {m.relative_to(root).as_posix()}")
    print("\nSeed miss dengan: python3 scripts/graphify-semantic-seed.py --seed <file> <analysis.json>")
    return 1 if misses else 0


if __name__ == "__main__":
    sys.exit(main())
