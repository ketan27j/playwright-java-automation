---
description: Migrate one .NET job to Java via the orchestrated pipeline
agent: orchestrator
---

Migrate a single .NET job to Java.

DOTNET_JOB and JAVA_REF are provided here: $ARGUMENTS

Run the pipeline as defined in your instructions:
init → dotnetscan → javascan (skip if .migration/idioms.md exists) →
HUMAN GATE 1 (contract + approach approval) → translate (per unit) →
verify (two gates) → unittest → escalate on any capped failure →
HUMAN GATE 3 (final risk review).

Stop and wait for the user at every human gate. Read back only short summaries
from subagents. Never read or write code yourself.
