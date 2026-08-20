#!/usr/bin/env python3
"""Add @Tag and @Operation annotations to Spring controllers."""

import re
import sys
from pathlib import Path

# Domain tag mapping based on controller path
DOMAIN_TAGS = {
    "master": "Master Data",
    "profil": "Profil Pegawai",
    "pegawai": "Data Pegawai",
    "cuti": "Cuti",
    "kepegawaian": "Kepegawaian",
    "penggajian": "Penggajian",
    "laporan": "Laporan",
    "auth": "Autentikasi",
    "system": "Sistem",
}

# Method summary mapping
METHOD_SUMMARIES = {
    "index": "List data dengan paginasi",
    "list": "Daftar semua data",
    "findById": "Detail data berdasarkan ID",
    "getById": "Ambil data berdasarkan ID",
    "save": "Simpan data baru",
    "create": "Buat data baru",
    "update": "Perbarui data",
    "delete": "Hapus data",
    "deleteById": "Hapus data berdasarkan ID",
    "patch": "Perbarui sebagian data",
}

def derive_tag_name(file_path: Path, request_mapping: str) -> str:
    """Derive OpenAPI tag name from request mapping path."""
    # Extract domain from path
    parts = request_mapping.strip("/").split("/")
    domain = parts[0] if parts else "other"
    
    # Build a readable name from the controller filename
    stem = file_path.stem
    # Remove "Controller" suffix
    name = stem.replace("Controller", "")
    # Convert CamelCase to spaced words
    name = re.sub(r'([A-Z])', r' \1', name).strip()
    
    # Add domain prefix
    domain_label = DOMAIN_TAGS.get(domain, domain.title())
    return f"{domain_label} — {name}"

def derive_summary(method_name: str, http_method: str, request_mapping: str) -> str:
    """Derive operation summary from method name and HTTP method."""
    # Check known method names
    if method_name in METHOD_SUMMARIES:
        return METHOD_SUMMARIES[method_name]
    
    # Build from method name
    # Convert camelCase to readable
    readable = re.sub(r'([A-Z])', r' \1', method_name).strip().lower()
    
    # Prefix with HTTP method action
    http_actions = {
        "get": "Ambil",
        "post": "Simpan",
        "put": "Perbarui",
        "delete": "Hapus",
        "patch": "Perbarui sebagian",
    }
    
    for prefix, action in http_actions.items():
        if readable.startswith(prefix):
            rest = readable[len(prefix):].strip()
            if rest:
                return f"{action} {rest}"
            break
    
    return readable

def process_controller(file_path: Path) -> dict:
    """Process a single controller file."""
    content = file_path.read_text()
    
    # Check if already has @Tag
    if "@Tag(" in content:
        return {"file": file_path.name, "status": "skipped", "reason": "already has @Tag"}
    
    # Check if has @RestController
    if "@RestController" not in content:
        return {"file": file_path.name, "status": "skipped", "reason": "no @RestController"}
    
    # Find @RequestMapping value
    rm_match = re.search(r'@RequestMapping\("([^"]+)"\)', content)
    if not rm_match:
        return {"file": file_path.name, "status": "skipped", "reason": "no @RequestMapping"}
    
    request_mapping = rm_match.group(1)
    tag_name = derive_tag_name(file_path, request_mapping)
    
    # Add @Tag import if not present
    if "import io.swagger.v3.oas.annotations.tags.Tag;" not in content:
        # Add after last import
        last_import_idx = content.rfind("import ")
        end_of_import = content.index("\n", last_import_idx)
        content = content[:end_of_import+1] + "import io.swagger.v3.oas.annotations.Operation;\nimport io.swagger.v3.oas.annotations.tags.Tag;\n" + content[end_of_import+1:]
    
    if "import io.swagger.v3.oas.annotations.Operation;" not in content:
        last_import_idx = content.rfind("import ")
        end_of_import = content.index("\n", last_import_idx)
        content = content[:end_of_import+1] + "import io.swagger.v3.oas.annotations.Operation;\n" + content[end_of_import+1:]
    
    # Add @Tag before @RestController or before the class declaration
    # Find the line with @RestController
    lines = content.split("\n")
    new_lines = []
    tag_added = False
    
    for i, line in enumerate(lines):
        if not tag_added and "@RestController" in line:
            # Find the indentation
            indent = line[:len(line) - len(line.lstrip())]
            new_lines.append(f'{indent}@Tag(name = "{tag_name}")')
            tag_added = True
        new_lines.append(line)
    
    content = "\n".join(new_lines)
    
    # Add @Operation to each endpoint method
    # Pattern: @GetMapping/PostMapping/etc followed by method declaration
    http_methods = ["GetMapping", "PostMapping", "PutMapping", "DeleteMapping", "PatchMapping"]
    
    lines = content.split("\n")
    new_lines = []
    operations_added = 0
    
    for i, line in enumerate(lines):
        stripped = line.strip()
        
        # Check if this line has an HTTP method annotation
        found_http = None
        for hm in http_methods:
            if f"@{hm}" in stripped:
                found_http = hm
                break
        
        if found_http:
            # Check if @Operation already exists on previous non-empty line
            prev_idx = i - 1
            while prev_idx >= 0 and lines[prev_idx].strip() == "":
                prev_idx -= 1
            
            if prev_idx >= 0 and "@Operation" in lines[prev_idx]:
                new_lines.append(line)
                continue
            
            # Find the method name - look ahead for the method signature
            method_name = "method"
            for j in range(i+1, min(i+5, len(lines))):
                m = re.search(r'(?:public|private|protected)\s+\S+\s+(\w+)\s*\(', lines[j])
                if m:
                    method_name = m.group(1)
                    break
            
            # Extract path from mapping annotation
            path_match = re.search(r'@' + found_http + r'\s*\(\s*"(.*?)"\s*\)', stripped)
            if not path_match:
                path_match = re.search(r'@' + found_http + r'\s*\(\s*\)', stripped)
                endpoint_path = ""
            else:
                endpoint_path = path_match.group(1)
            
            summary = derive_summary(method_name, found_http, endpoint_path)
            
            # Find indentation
            indent = line[:len(line) - len(line.lstrip())]
            new_lines.append(f'{indent}@Operation(summary = "{summary}")')
            operations_added += 1
        
        new_lines.append(line)
    
    content = "\n".join(new_lines)
    file_path.write_text(content)
    
    return {"file": file_path.name, "status": "done", "tag": tag_name, "operations": operations_added}


def main():
    src_dir = Path("src/main/java/id/perumdamts/kepegawaian/controllers")
    
    results = {"done": 0, "skipped": 0, "total_ops": 0}
    
    for controller_file in sorted(src_dir.rglob("*Controller.java")):
        result = process_controller(controller_file)
        status = result["status"]
        if status == "done":
            results["done"] += 1
            results["total_ops"] += result.get("operations", 0)
            print(f"  ✅ {result['file']}: @Tag({result['tag']}) + {result.get('operations', 0)} @Operation")
        else:
            results["skipped"] += 1
            print(f"  ⏭️  {result['file']}: {result.get('reason', 'unknown')}")
    
    print(f"\n📊 Done: {results['done']} controllers, {results['total_ops']} @Operation annotations added")
    print(f"   Skipped: {results['skipped']}")


if __name__ == "__main__":
    main()
