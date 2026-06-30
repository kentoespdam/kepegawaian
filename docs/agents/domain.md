# Domain Docs

How the engineering skills should consume this repo's domain documentation when exploring the codebase.

## Lazy Read Protocol

**Read `CONTEXT-MAP.md` first, then only the relevant sub-context file(s).** Do NOT load all sub-context files at once.

| If working on... | Read |
|------------------|------|
| `master/` (Profesi, Jabatan, Organisasi) | [`docs/context/language-master.md`](../context/language-master.md) |
| `pegawai/` — terminology | [`docs/context/language-pegawai.md`](../context/language-pegawai.md) |
| `pegawai/` or `kepegawaian/` — architecture decisions | [`docs/context/decisions-pegawai.md`](../context/decisions-pegawai.md) |
| `profil/` (biodata, pendidikan, updateProfile) | [`docs/context/language-profil.md`](../context/language-profil.md) |
| `cuti/` — terminology | [`docs/context/language-cuti.md`](../context/language-cuti.md) |
| `cuti/` — architecture decisions | [`docs/context/decisions-cuti.md`](../context/decisions-cuti.md) |
| Auth, JWT, Spring profiles | [`docs/context/language-security.md`](../context/language-security.md) |
| Cross-module relations / dependency direction | [`docs/context/relationships.md`](../context/relationships.md) |
| Domain examples or flagged ambiguities | [`docs/context/examples-and-flags.md`](../context/examples-and-flags.md) |

## ADRs

- **`docs/adr/`** — read ADRs that touch the area you're about to work in.

If any of these files don't exist, **proceed silently**. Don't flag their absence upfront.

## File Structure

This is a **multi-context** repo (CONTEXT-MAP.md at root pointing to per-topic context files):

```
/
├── CONTEXT.md               ← entry point (pointer only)
├── CONTEXT-MAP.md           ← lazy-read index
├── docs/
│   ├── adr/                 ← architectural decisions
│   └── context/
│       ├── language-master.md
│       ├── language-pegawai.md
│       ├── language-profil.md
│       ├── language-cuti.md
│       ├── language-security.md
│       ├── relationships.md
│       ├── decisions-pegawai.md
│       ├── decisions-cuti.md
│       └── examples-and-flags.md
└── src/
```

## Use the Glossary's Vocabulary

When your output names a domain concept (in an issue title, a refactor proposal, a hypothesis, a test name), use the term as defined in the relevant sub-context file. Don't drift to synonyms the glossary explicitly avoids.

If the concept you need isn't in the glossary yet, that's a signal — either you're inventing language the project doesn't use (reconsider) or there's a real gap (note it for `/domain-modeling`).

## Flag ADR Conflicts

If your output contradicts an existing ADR, surface it explicitly rather than silently overriding:

> _Contradicts ADR-0002 (selective three-tier Envers audit) — but worth reopening because…_
