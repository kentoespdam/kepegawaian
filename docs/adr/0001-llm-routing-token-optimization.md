# ADR-0001: LLM Routing untuk Token Optimization

**Status**: Accepted  
**Tanggal**: 2026-10-09  
**Konteks**: Project `kepegawaian` — Antigravity AI Agent

## Keputusan

Implementasikan LLM Routing dengan mandatory classification di GEMINI.md untuk mengoptimalkan penggunaan token/credit AI.

## Konteks & Masalah

Root agent (Flash) sebelumnya menangani semua task dengan model yang sama, menyebabkan:
- Token waste: task I/O sederhana diproses model berat
- Hero Fallacy: Flash menjawab task kompleks sendiri tanpa eskalasi ke Pro
- Subagent sprawl: spawn agent baru per-task membunuh cache hit
- Ghost Session: GEMINI.md tidak terload karena 20,000 token budget limit Antigravity

## Keputusan Spesifik

1. **Routing deterministik** via mandatory classification [LITE]/[PRO]/[DIRECT] — bukan penilaian subjektif Flash
2. **Model tier**: Lite Worker=`flash_lite`, Pro Reasoner=`pro`/`inherit`, Root=`flash`
3. **Blind Forwarding**: Root hanya baca blok `<<<DISPATCH>>>`, reasoning Pro diabaikan
4. **Pass-by-Reference**: Payload via ARTIFACT path, bukan raw content
5. **Task ambiguous**: Split sequential LITE→PRO
6. **Subagent reuse**: `send_message` ke idle agent, spawn baru hanya jika killed/context penuh
7. **Autoload fix**: `@[LLM Routing Rules](GEMINI.md)` di AGENTS.md mencegah demotion ke path pointer

## Konsekuensi

**Positif**: Estimasi hemat token 40–70% untuk task I/O. Cache hit meningkat via subagent reuse. Hero Fallacy dieliminasi.

**Diterima**: Latency tambahan per routing chain. Kompleksitas GEMINI.md bertambah ~50 baris.

## Glosarium

| Term | Definisi |
|---|---|
| `[LITE]` | Tier I/O — file ops, commands, scripts |
| `[PRO]` | Tier reasoning — arsitektur, keamanan, debugging kompleks |
| `[DIRECT]` | Tier inline — jawaban trivial tanpa subagent |
| Blind Forwarding | Root hanya baca DISPATCH block, abaikan reasoning Pro |
| DISPATCH block | Blok `<<<DISPATCH>>>...<<<END_DISPATCH>>>` output Pro |
| Subagent Reuse | `send_message` ke subagent idle vs `invoke_subagent` baru |
| Ghost Session | GEMINI.md tidak terload karena 20,000 token budget exceeded |
