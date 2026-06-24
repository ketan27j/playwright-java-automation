# .NET → Java migration agent pack for OpenCode

Drop the `.opencode/` folder and `opencode.json` into the parent folder that
contains BOTH codebases (the .NET folder and the Java output folder), e.g.:

```
my-migration/
├── opencode.json
├── .opencode/
│   ├── agents/        migrate.md (primary) · scan.md · translate.md · verify.md
│   ├── commands/      migrate-next.md
│   └── context/       mapping-cheatsheet.md
├── migration/         (created by @scan: state.json, inventory.md, errors/)
├── DotNetApp/         your ASP.NET MVC/Web API + EF6 source
└── java-app/          your Java 21 + Spring Boot target
```

## Setup (5 minutes)

1. Adjust model IDs in `opencode.json` to match what `ollama list` shows on
   your machine (and the baseURL if you serve via LM Studio/llama.cpp instead).
2. Open `.opencode/context/mapping-cheatsheet.md` and fill the
   **Project conventions** block at the bottom (base package, Gradle vs Maven,
   Lombok yes/no, JSON casing). MERGE YOUR EXISTING SKILL FILE INTO THIS
   CHEATSHEET — but keep the result under ~300 lines. The translator reads it
   on every file; a 2,000-line skill file is what's been eating your context
   and confusing the small models.
3. Make sure the Java project compiles clean BEFORE starting
   (`./gradlew compileJava`). The verify loop only works if the baseline is green.

## Usage

```
opencode                     # in the parent folder
> Tab to select the "migrate" agent (or it's the default primary)
> /migrate-next              # first run: builds inventory, then stop
> /migrate-next 4            # each run: translates + verifies 4 files
```

Run one batch, then **start a fresh session** (`/new`) for the next batch.
All progress lives in `migration/state.json` on disk, so sessions are
disposable — that, plus subagents getting their own context windows, is what
keeps you under the 40% context ceiling.

## Why your accuracy was 20–30%, and what changes it

1. **One file per translator invocation, fresh context.** Local Qwen/Gemma-class
   models degrade sharply past ~10–15k tokens of mixed instructions+code. Each
   `@translate` call now sees: cheatsheet (~3k tokens) + one .cs file +
   grep'd signatures of dependencies. Nothing else.
2. **The compile loop is the real accuracy engine.** First-shot Java from a
   local model will always be rough; `@verify` feeding javac errors back in
   ≤3 tight rounds is what converts 30% correct into 80–90% compiling code.
3. **Mechanical mapping tables instead of prose guidance.** Small models follow
   "X → Y" tables far better than paragraph-style best practices. Extend the
   cheatsheet every time you manually fix a recurring mistake — it compounds.
4. **Temperature 0 for translate/verify.** Migration is transcription, not
   creativity.
5. **Dependency order (domain → data → service → web)** means each file's
   imports already exist, eliminating a whole class of hallucinated classes.
6. **TODO-MIGRATION markers instead of silent guesses.** `grep -rn TODO-MIGRATION
   java-app/` gives you the exact human-review worklist; the model is told it's
   always allowed to flag instead of invent.

## Hard limits to respect

- Compiling ≠ correct. Plan a behavioral check per vertical slice: hit the same
  endpoint on the .NET app and the Java app, diff the JSON. Even a crude
  curl-and-diff script catches semantic drift (rounding, casing, null handling)
  that no compile loop will.
- Razor views, auth/security code, and stored-proc-heavy data access are
  flagged `manual` — review those by hand.
- If qwen3-coder still struggles on big service classes, split them: the
  translate agent already chunks files >400 lines, but you can also lower the
  batch size to 2–3.

## Tuning knobs

- `num_ctx`: make sure Ollama serves the model with at least 32k context
  (`OLLAMA_CONTEXT_LENGTH=32768` or a Modelfile `PARAMETER num_ctx 32768`).
  Ollama's default 4–8k silently truncates your prompt — a very common cause
  of garbage output that looks like "model is dumb".
- Assign your weakest model only to @scan (it's grep-driven). Keep your
  strongest coder on @translate and @verify.
- If a file fails 3 verify rounds, it lands in `migration/errors/` — fix that
  one interactively, mark it `verified` in state.json, continue.

## Migrating batch jobs (console apps, Windows services, Quartz/Hangfire, scheduled tasks)

These do NOT become plain services — they become Spring Batch jobs / @Scheduled.
For the generated batch code to match your existing jobs, give scan ONE working
Java batch job to copy:

- When you start the orchestrator, name the reference, e.g.:
  "Migrate the next batch. .NET source ./DotNetApp, Java target ./java-app,
   base package com.yourco.app. Reference batch job to imitate:
   ./java-app/src/main/java/com/yourco/app/batch/InvoiceSyncJob.java"
- scan reads that file FULLY, records its exact shape (chunk vs Tasklet,
  reader/processor/writer, JobRepository/tx wiring, trigger, chunk size, naming,
  and the Spring Batch API version it uses) into migration/java-conventions.md.
- translate then copies that shape for every batch-layer file instead of
  inventing a generic (possibly wrong-version) Spring Batch style.

If scan finds batch-layer .NET files but you gave no reference AND the repo has
no existing batch job, it flags this and you should supply a reference before
translating those files — otherwise the batch output is a guess.

## Deep batch-job pipeline (separate from the bulk migration)

Batch jobs run through a richer, quality-gated pipeline instead of the simple
translate→verify loop, because they're logic-dense and a generic Spring Batch
shape rarely matches your repo.

Pipeline per file (one file fully through before the next):
1. **Reference analyzer** (once) reads your reference batch job + existing tests →
   `migration/batch-template.md` (canonical job shape + detected test harness).
2. **Plan** — reads the .NET job fully → `migration/plans/<Job>.plan.md`: purpose,
   trigger, full business-logic breakdown, Spring Batch mapping, and test scenarios
   derived from the .NET source.
3. **Translate** — writes the Java job from the plan, copying the reference shape.
4. **Build** (objective gate) — compile + fix, max 3 rounds.
5. **Review → Fix** (loop, max 2) — reviewer scores the Java against the plan + a
   fixed rubric, writes findings into the plan; fixer resolves them; build re-runs
   after every fix.
6. **Test** (objective gate) — tests derived from the .NET source + plan (never from
   the Java output, so they aren't tautological), run, one fix round on failure.

Each file's `stage` (pending → planned → translated → built → reviewed → tested →
done, or needs-human) lives in `migration/state.json`, so sessions are disposable
and each subagent runs in a fresh context.

### Run it
- OpenCode: switch to the `batch-migrate` agent (or `/migrate-batch <reference-path>`).
- RooCode: switch to 🧭 Batch Pipeline Orchestrator; tell it the reference batch
  job path and let it delegate via new_task. Bind your strongest local model to the
  reviewer and test-writer modes — those are the hardest for small models.

### Guardrails that make the gates meaningful
- The test-writer derives expected values from the plan/source, NOT the Java code.
- The fixer is forbidden from weakening a test or deleting logic to pass a check;
  build re-runs after every fix so a "green" review/test can't hide a broken build.
- Non-convergence (build fails after 3, review after 2, test after 1 fix) → the file
  is marked `needs-human` and the pipeline moves on rather than looping forever.

### Golden-sample hook (optional, strongest check)
If you can ever run the legacy .NET job, drop input + expected-output fixtures in
`migration/golden/<JobName>/`. The test-writer will then assert byte-for-byte
parity — the only thing that reliably catches rounding/ordering/null drift. Without
it, tests assert plan-derived expected behavior (good, but not parity-proven).
