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
