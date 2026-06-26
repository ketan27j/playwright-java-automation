# .NET → Java migration agent pack (OpenCode)

An orchestrator + 5 subagents that migrate one .NET job to Java at a time, built
to run on local models (qwen-coder-next / qwen3 / gpt-oss) with a clean division
of labor and a real correctness oracle.

## Why this should beat your current 20–30%

Your old pipeline had no correctness oracle — `translate` inferred behavior from
structure, and `verify` only checked that it compiled. This pack fixes that at
three points:

1. **`dotnetscan` emits a behavior *contract*, not a structure dump.** The
   business rules are written out in plain language plus the type-semantics traps
   (decimal, DateTime, null, overflow). That's the spec `translate` implements,
   so the weak model stops guessing.
2. **`verify` is split into two gates.** A compile gate that is *forbidden* from
   stubbing/deleting logic to go green, and a behavior gate that uses golden-master
   replay (if the .NET job is runnable) or source-derived checks. This is your
   single biggest correctness lever.
3. **`unittest` derives expected values from .NET, never from the generated Java**,
   so tests catch the bug instead of encoding it.

## Files

```
.opencode/
  agent/
    orchestrator.md   # primary: plans, tracks, delegates; never touches code
    dotnetscan.md     # .NET job → behavior contract
    javascan.md       # reference Java job → cached idiom reference (run once)
    translate.md      # per-unit: contract + idioms → Java
    verify.md         # compile gate + behavior gate
    unittest.md       # tests with .NET-derived expectations
  command/
    migrate.md        # /migrate entry point
AGENTS.md             # layout + context-hygiene rules (read by all agents)
README.md
```

## Install

Copy `.opencode/` and `AGENTS.md` into your project root (the folder that contains
both `dotnet/` and `java/`). Restart OpenCode so it picks up the agents.

> Note on the command folder: recent OpenCode uses `.opencode/command/`. If your
> version doesn't pick up `/migrate`, try renaming the folder to
> `.opencode/commands/` — both names have appeared across versions.

## Set your models

Every agent has a `model:` line using a placeholder like `ollama/qwen-coder-next`.
Replace each with the exact provider/model id you registered in `opencode.json`.
Recommended assignment:

- **orchestrator** → `gpt-oss` (or qwen3): rewards instruction-following + terseness.
- **dotnetscan / javascan / translate / verify / unittest** → `qwen-coder-next`:
  your strongest coder, where the real work happens.

## Run

```
/migrate dotnet/SettlementJob java/src/main/java/com/firm/batch/reconjob
```

First arg = the .NET job to migrate. Second = an already-migrated reference job in
`java/` (used once to build `.migration/idioms.md`, then cached for all later jobs).

## Context usage

There's no hard context cap in this pack — subagents read whatever they need to be
correct. What keeps the run clean is the *structure*, not rationing:

- **Isolation**: every subagent runs in its own fresh session, so scan/translate/
  verify contexts stay separated and the run is resumable.
- **Paths not blobs**: big artifacts live in `.migration/`; agents exchange file
  paths and concise summaries, which keeps the orchestrator focused on the plan
  rather than ingesting code.
- **Per-unit translation**: for focus and resumability, so one unit's failure
  doesn't muddy the others.

If a worker ever does balloon, it's usually because it re-read the whole project
when it only needed a few files — tighten the specific instruction, don't add
agents. But correctness comes first: an agent should always read enough to be right.

## Human-in-the-loop gates

| Gate | Where | Required? | Why |
|------|-------|-----------|-----|
| **1. Contract + approach approval** | after `dotnetscan`, before `translate` | **Yes** | Cheapest place to kill divergence. Approve a one-page contract instead of reviewing wrong generated code later. |
| **2. Escalation on capped failure** | when verify/unittest hits its loop cap | Conditional | Stops the system forcing a fake pass. You decide how to proceed. |
| **3. Final risk review** | after all gates pass | **Yes** | Diff-review only the units the contract marked high-risk, then merge. |

Skipped on purpose: no gate after `javascan` (idioms are stable, low value) and no
per-unit translate review (the gates cover that; per-unit is too granular).

## The one thing that most changes results

Whether you can execute the .NET job to capture golden-master I/O. If you can,
drop representative input/output pairs in `.migration/<JOB>/golden/` (or let
`dotnetscan` capture them when you approve the gated `dotnet run`). The behavior
gate then becomes a real diff against real .NET output, and correctness should jump
well past 30%. If you can't, the pipeline falls back to source-derived assertions
and leans harder on Human Gate 1.

## Scaling to many jobs

This pack migrates one job per `/migrate` invocation — matching how you described
the input. Because `idioms.md` is cached after the first run, later jobs skip the
Java scan automatically. To batch, wrap `/migrate` in a loop over your job list;
keep Human Gate 1 per job (it's the cheap one that protects correctness).
