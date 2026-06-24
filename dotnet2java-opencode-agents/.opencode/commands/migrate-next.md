---
description: Migrate the next batch of pending .NET files to Java
agent: migrate
---

Read migration/state.json. If it does not exist, run Phase 0 (bootstrap via @scan) and stop after reporting the inventory.

Otherwise: pick the next batch of at most $ARGUMENTS pending files (default 4) in dependency order, run Phase 1 (@translate per file) and Phase 2 (@verify for the batch), update migration/state.json, and print the Phase 3 summary. Then stop.

Remember: never read source code yourself; pass only file paths to subagents.
