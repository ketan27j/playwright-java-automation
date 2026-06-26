# Migration workspace conventions

This repo holds two codebases side by side and a migration pipeline that converts
.NET jobs to Java one at a time.

## Layout

- `dotnet/` — existing .NET source. **Read-only.** Never modified by any agent.
- `java/`   — target Java project. New code is written here. Contains at least one
  already-migrated **reference job** used to learn house style.
- `.migration/` — all migration artifacts (contracts, idioms, state, reports,
  golden masters). Agents write here freely; this is not shipped code.

## Migration artifacts

- `.migration/idioms.md` — house style, learned once from the reference job, reused
  for every job. Job-independent.
- `.migration/<JOB>/contract.md` — behavior contract for one job.
- `.migration/<JOB>/state.md` — orchestrator's plan + status log.
- `.migration/<JOB>/verify-report.md`, `test-report.md` — gate outputs.
- `.migration/<JOB>/golden/` — optional captured .NET input/output pairs.

## Context hygiene (sensible, not starving)

There is no hard context cap — read what you need to be correct. The goal is a
clean division of labor, not rationing tokens.

1. **Read enough to be right.** When a business rule or type behavior is unclear,
   read the body. An accurate contract and a faithful translation beat a fast one.
2. **Pass paths, not blobs, between agents.** Large outputs (contracts, idioms,
   reports) live in `.migration/` and on the project tree. Agents hand each other
   file paths and concise summaries instead of pasting big bodies back and forth —
   this keeps the orchestrator focused on the plan, not because tokens are scarce.
3. **Subagents run in isolated sessions.** Each scan/translate/verify/test runs in
   its own fresh context, so work stays cleanly separated and the run is resumable.
4. **Per-unit work is for focus and resumability**, so a failure in one unit
   doesn't muddy the others — not a token-saving constraint.

## Money, dates, nulls — the usual translation traps

Default rules (the idioms file may override per project):
- .NET `decimal` → `BigDecimal` with explicit scale + rounding. **Never `double`.**
- `DateTime`/`DateTimeOffset` → an explicit temporal type with an explicit zone.
- Preserve null semantics; do not let nullable value types collapse silently.
- Watch integer width and overflow behavior.
