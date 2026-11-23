# Panduan & Rekomendasi Implementasi Frontend: Struktur Organisasi (Org Chart)

> Dokumen panduan implementasi untuk tim **Frontend (`kepegawaian-fe`)**: visualisasi pohon hierarki Struktur Organisasi Perusahaan (SO) menggunakan library open-source **`d3-org-chart`** yang mengonsumsi endpoint backend `GET /laporan/kepegawaian/so`.

| Item | Nilai |
|------|-------|
| Target Modul FE | `kepegawaian-fe` (Next.js 16 + React 19 + Tailwind CSS) |
| Rekomendasi Library | [`d3-org-chart`](https://github.com/bumbeishvili/d3-org-chart) (MIT License / 100% Gratis & Open Source) |
| Endpoint Backend | `GET /laporan/kepegawaian/so` |
| ADR Terkait | [ADR-0061: Struktur Organisasi Chart Flat Payload untuk d3-org-chart](../adr/0061-struktur-organisasi-chart-flat-payload-d3-org-chart.md) |
| Dokumentasi Domain | [language-laporan.md](../context/language-laporan.md) |

---

## 1. Ringkasan & Arsitektur

Pada aplikasi legacy (SmartOffice PHP), visualisasi struktur organisasi menggunakan library komersial **GoJS** di dalam iframe. Untuk aplikasi modern Next.js, implementasi direkomendasikan menggunakan **`d3-org-chart`** dengan pertimbangan:
1. **100% Open Source (MIT License)**: Bebas digunakan tanpa biaya lisensi dan tanpa watermark.
2. **Kesesuaian Fitur Screenshot**:
   - Tampilan pohon hierarki terstruktur (Dewan Pengawas & Direksi $\rightarrow$ Manajer $\rightarrow$ Supervisor).
   - Fitur **Pencarian Pejabat/Jabatan** dengan penyorotan (highlight) dan *auto-center/zoom*.
   - Fitur **Pan & Zoom** interaktif untuk navigasi bagan skala besar.
   - Fitur **Expand / Collapse** cabang anak.
   - Fitur **Export** instan ke PNG, SVG, atau PDF.
3. **Format Data Flat**: Mengonsumsi data datar (*flat array*) berbasis relasi `id` dan `parentId` tanpa perlu rekursi berat di sisi browser.

---

## 2. Kontrak Endpoint Backend

### Detail Request
* **URL**: `/laporan/kepegawaian/so`
* **Method**: `GET`
* **Headers**: `Authorization: Bearer <token>`
* **Izin Akses**: Membutuhkan authority `LAPORAN:READ` atau `PEGAWAI:READ` (atau role `ADMIN`).
* **Query Parameters**:
  | Parameter | Tipe | Default | Keterangan |
  |-----------|------|---------|------------|
  | `maxLevel` | `Integer` | `6` | Membatasi kedalaman struktur. Default `6` mencakup Supervisor (Level 1: Dewan Pengawas, 2: Direktur Utama, 3–4: Direktur, 5: Manajer, 6: Supervisor). Level 7 (Staf) dikecualikan secara default. |
  | `format` | `String` | `flat` | `flat` (default untuk `d3-org-chart`) atau `tree` (jika membutuhkan nested hierarki `children`). |

### Schema Data Response (`format=flat`)

Response dibungkus dalam envelope standar backend `ListResult<SoResponse>`:

```jsonc
{
  "status": 200,
  "statusText": "OK",
  "data": [
    {
      "id": 1,
      "parentId": null, // null untuk posisi teratas (Dewan Pengawas)
      "kodeJabatan": "0",
      "namaJabatan": "Kepala Dewan Pengawas",
      "level": 1,
      "namaPegawai": "Drs. H. Suwandi, M.M.",
      "nipam": "8901001",
      "namaOrganisasi": "DEWAN PENGAWAS",
      "fotoProfil": null,
      "isLowong": false
    },
    {
      "id": 2,
      "parentId": 1,
      "kodeJabatan": "1",
      "namaJabatan": "Direktur Utama",
      "level": 2,
      "namaPegawai": "Priyo Supriyono, S.T.",
      "nipam": "8902001",
      "namaOrganisasi": "DIREKTORAT UTAMA",
      "fotoProfil": "https://.../foto.jpg",
      "isLowong": false
    },
    {
      "id": 27,
      "parentId": 26,
      "kodeJabatan": "1.2.1.1",
      "namaJabatan": "Supervisor Anggaran & Pelaporan",
      "level": 6,
      "namaPegawai": null, // null jika posisi belum terisi pejabat aktif
      "nipam": null,
      "namaOrganisasi": "BAGIAN KEUANGAN",
      "fotoProfil": null,
      "isLowong": true // Indikator posisi jabatan kosong
    }
  ],
  "timestamp": "2026-11-23 14:15:00"
}
```

---

## 3. Langkah Instalasi di Frontend (`kepegawaian-fe`)

Jalankan instalasi dependensi di root proyek `kepegawaian-fe`:

```bash
pnpm add d3 d3-org-chart d3-flextree
pnpm add -D @types/d3
```

> **Catatan**: `d3-flextree` adalah peer-dependency yang diwajibkan oleh `d3-org-chart` untuk kalkulasi layout hirarkis fleksibel.

---

## 4. Implementasi Komponen di Frontend

### A. Tipe TypeScript (`src/types/laporan/so.ts`)

```typescript
export interface OrgChartNode {
  id: number;
  parentId: number | null;
  kodeJabatan: string;
  namaJabatan: string;
  level: number;
  namaPegawai: string | null;
  nipam: string | null;
  namaOrganisasi: string | null;
  fotoProfil: string | null;
  isLowong: boolean;
}
```

---

### B. Komponen Visualisasi (`src/components/laporan/org-chart.tsx`)

Komponen client-side wrapper lengkap dengan pencarian, kontrol zoom, dan styling card:

```tsx
"use client";

import React, { useEffect, useRef, useState } from "react";
import { OrgChart } from "d3-org-chart";
import { OrgChartNode } from "@/types/laporan/so";
import { Search, ZoomIn, ZoomOut, Maximize2, Download, RefreshCw } from "lucide-react";

interface OrgChartProps {
  data: OrgChartNode[];
  onRefresh?: () => void;
  isLoading?: boolean;
}

export function OrgChartView({ data, onRefresh, isLoading }: OrgChartProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const chartRef = useRef<OrgChart<OrgChartNode> | null>(null);
  const [searchTerm, setSearchTerm] = useState("");

  useEffect(() => {
    if (!containerRef.current || !data || data.length === 0) return;

    if (!chartRef.current) {
      chartRef.current = new OrgChart<OrgChartNode>();
    }

    const chart = chartRef.current;

    chart
      .container(containerRef.current)
      .data(data)
      .nodeId((d) => String(d.id))
      .parentNodeId((d) => (d.parentId ? String(d.parentId) : null))
      .nodeWidth(() => 240)
      .nodeHeight(() => 110)
      .childrenMargin(() => 50)
      .compactMarginBetween(() => 25)
      .compactMarginPair(() => 50)
      .neighbourMargin(() => 25)
      // Custom HTML Card Template (sesuai tema screenshot)
      .nodeContent((d) => {
        const isVacant = d.data.isLowong;
        const bgHeader = isVacant ? "#fed7aa" : "#bae6fd"; // Amber jika lowong, Sky blue jika terisi
        const borderCol = isVacant ? "#f97316" : "#0284c7";

        return `
          <div style="
            width: 100%;
            height: 100%;
            background-color: #ffffff;
            border: 2px solid ${borderCol};
            border-radius: 8px;
            box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1);
            display: flex;
            flex-direction: column;
            overflow: hidden;
            font-family: inherit;
          ">
            <!-- Header Jabatan -->
            <div style="
              background-color: ${bgHeader};
              padding: 6px 8px;
              text-align: center;
              font-weight: 600;
              font-size: 13px;
              color: #0f172a;
              border-bottom: 1px solid rgba(0,0,0,0.08);
              white-space: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;
            ">
              ${d.data.namaJabatan}
            </div>

            <!-- Body Informasi Pegawai -->
            <div style="
              flex: 1;
              display: flex;
              flex-direction: column;
              align-items: center;
              justify-content: center;
              padding: 6px 8px;
              text-align: center;
              background-color: #ffffff;
            ">
              ${
                isVacant
                  ? `
                  <span style="font-size: 12px; color: #ea580c; font-weight: 500; font-style: italic;">
                    (Jabatan Lowong)
                  </span>
                `
                  : `
                  <span style="font-size: 12px; font-weight: 600; color: #1e293b; max-width: 220px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                    (${d.data.namaPegawai ?? "-"})
                  </span>
                  <span style="font-size: 11px; color: #64748b; margin-top: 2px;">
                    NIPAM: ${d.data.nipam ?? "-"}
                  </span>
                `
              }
              <span style="font-size: 10px; color: #94a3b8; margin-top: 3px;">
                ${d.data.namaOrganisasi ?? ""}
              </span>
            </div>
          </div>
        `;
      })
      .render();

    // Pastikan chart otomatis terpusat saat pertama render
    chart.fit();
  }, [data]);

  // Handler Pencarian Node
  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (!chartRef.current || !searchTerm.trim()) {
      chartRef.current?.clearHighlighting();
      return;
    }

    const term = searchTerm.toLowerCase();
    const { allNodes } = chartRef.current.getChartState();
    const matches = allNodes.filter((node) => {
      const nama = node.data.namaPegawai?.toLowerCase() ?? "";
      const jabatan = node.data.namaJabatan.toLowerCase();
      const nipam = node.data.nipam?.toLowerCase() ?? "";
      return nama.includes(term) || jabatan.includes(term) || nipam.includes(term);
    });

    if (matches.length > 0) {
      chartRef.current.clearHighlighting();
      matches.forEach((node) => {
        chartRef.current?.setHighlighted(node.data.id);
        chartRef.current?.expandId(node.data.id);
      });
      // Fokuskan viewport ke node pertama yang cocok
      chartRef.current.fit({ nodes: [matches[0]] });
    } else {
      alert("Pegawai atau jabatan tidak ditemukan.");
    }
  };

  return (
    <div className="relative w-full h-[800px] bg-slate-50 border border-slate-200 rounded-xl overflow-hidden shadow-sm">
      {/* Top Floating Toolbar */}
      <div className="absolute top-4 left-4 z-20 flex flex-wrap gap-2 items-center bg-white/95 backdrop-blur-sm p-2 rounded-lg border border-slate-200 shadow-md">
        {/* Form Pencarian */}
        <form onSubmit={handleSearch} className="flex items-center gap-1.5">
          <div className="relative">
            <Search className="w-4 h-4 absolute left-2.5 top-2.5 text-slate-400" />
            <input
              type="text"
              placeholder="Cari pejabat / jabatan..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-8 pr-3 py-1.5 text-sm bg-slate-100 border border-slate-200 rounded-md focus:outline-none focus:ring-2 focus:ring-sky-500 w-52 md:w-64"
            />
          </div>
          <button
            type="submit"
            className="px-3 py-1.5 text-sm font-medium bg-sky-600 text-white rounded-md hover:bg-sky-700 transition"
          >
            Cari
          </button>
        </form>

        <div className="h-6 w-px bg-slate-200 mx-1" />

        {/* Tombol Kontrol Navigasi */}
        <button
          onClick={() => chartRef.current?.zoomIn()}
          title="Zoom In"
          className="p-1.5 text-slate-600 hover:bg-slate-100 rounded-md transition"
        >
          <ZoomIn className="w-4 h-4" />
        </button>
        <button
          onClick={() => chartRef.current?.zoomOut()}
          title="Zoom Out"
          className="p-1.5 text-slate-600 hover:bg-slate-100 rounded-md transition"
        >
          <ZoomOut className="w-4 h-4" />
        </button>
        <button
          onClick={() => chartRef.current?.fit()}
          title="Reset / Fit View"
          className="p-1.5 text-slate-600 hover:bg-slate-100 rounded-md transition"
        >
          <Maximize2 className="w-4 h-4" />
        </button>
        <button
          onClick={() => chartRef.current?.exportImg({ full: true })}
          title="Export ke Gambar"
          className="p-1.5 text-slate-600 hover:bg-slate-100 rounded-md transition"
        >
          <Download className="w-4 h-4" />
        </button>
        {onRefresh && (
          <button
            onClick={onRefresh}
            disabled={isLoading}
            title="Refresh Data"
            className="p-1.5 text-slate-600 hover:bg-slate-100 rounded-md transition disabled:opacity-50"
          >
            <RefreshCw className={`w-4 h-4 ${isLoading ? "animate-spin" : ""}`} />
          </button>
        )}
      </div>

      {/* Kontainer DOM D3 Chart */}
      <div ref={containerRef} className="w-full h-full cursor-grab active:cursor-grabbing" />
    </div>
  );
}
```

---

### C. Pemanggilan Halaman via React Query (`src/app/(app)/laporan/struktur-organisasi/page.tsx`)

```tsx
"use client";

import React, { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { OrgChartView } from "@/components/laporan/org-chart";
import { OrgChartNode } from "@/types/laporan/so";
import { api } from "@/lib/api"; // Axios / Fetch client dengan bearer auth

export default function StrukturOrganisasiPage() {
  const [maxLevel, setMaxLevel] = useState<number>(6);

  const { data, isLoading, isError, refetch } = useQuery<OrgChartNode[]>({
    queryKey: ["laporan", "so", maxLevel],
    queryFn: async () => {
      // Panggil endpoint backend
      const res = await api.get<{ data: OrgChartNode[] }>(
        `/laporan/kepegawaian/so?maxLevel=${maxLevel}&format=flat`
      );
      return res.data.data;
    },
  });

  return (
    <div className="p-6 space-y-4">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Struktur Organisasi Perusahaan</h1>
          <p className="text-sm text-slate-500">
            Peta hierarki jabatan struktural dan persebaran pejabat aktif PDAM.
          </p>
        </div>

        {/* Filter Kedalaman Level */}
        <div className="flex items-center gap-2">
          <label htmlFor="maxLevel" className="text-sm font-medium text-slate-700">
            Kedalaman Struktur:
          </label>
          <select
            id="maxLevel"
            value={maxLevel}
            onChange={(e) => setMaxLevel(Number(e.target.value))}
            className="px-3 py-1.5 text-sm bg-white border border-slate-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-sky-500"
          >
            <option value={2}>Direksi Saja (Level 1–2)</option>
            <option value={5}>Hingga Manajer (Level 1–5)</option>
            <option value={6}>Hingga Supervisor (Level 1–6) — Rekomendasi</option>
            <option value={7}>Seluruh Karyawan & Staf (Level 1–7)</option>
          </select>
        </div>
      </div>

      {isLoading && (
        <div className="h-[600px] flex items-center justify-center bg-slate-50 rounded-xl border border-slate-200">
          <div className="text-slate-500 text-sm animate-pulse">Memuat data struktur organisasi...</div>
        </div>
      )}

      {isError && (
        <div className="p-4 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">
          Gagal mengambil data struktur organisasi. Pastikan Anda memiliki izin akses <code>LAPORAN:READ</code> atau <code>PEGAWAI:READ</code>.
        </div>
      )}

      {data && (
        <OrgChartView
          data={data}
          onRefresh={() => refetch()}
          isLoading={isLoading}
        />
      )}
    </div>
  );
}
```

---

## 5. Ringkasan Poin Penting untuk Developer FE

1. **Format Data Flat**: Jangan memformat data menjadi tree di sisi backend maupun frontend; operasikan array langsung ke `.data(data)`.
2. **Identifikasi Root**: Root node (Dewan Pengawas) memiliki `parentId = null`. Ini wajib agar algoritma layout D3 mengenali pucuk pohon.
3. **Penanganan Posisi Lowong**:
   - Jika `isLowong === true`, field `namaPegawai` bernilai `null`.
   - Card template di FE otomatis menampilkan label khusus `(Jabatan Lowong)` berwarna oranye untuk membedakannya dengan jabatan yang telah terisi.
4. **Pencarian Cerdas**:
   - Pencarian mencocokkan `namaPegawai`, `namaJabatan`, atau `nipam`.
   - Method `chart.fit({ nodes: [match] })` otomatis memperbesar dan menggeser viewport tepat ke posisi node yang dicari.
