---
description: Run the deep batch-migration pipeline for the next batch job
agent: batch-migrate
---

Run the batch migration pipeline.

If migration/batch-template.md does not exist, first run Phase A: invoke @batch-reference-analyzer with the reference batch job path: $ARGUMENTS (if empty, let it auto-detect an existing Java batch job; if none exists, stop and ask for a reference).

Then take the next incomplete `layer:"batch"` file in migration/state.json and run it through the full pipeline (plan → translate → build → review→fix → test), respecting all loop caps, updating its `stage`, and escalating to needs-human on non-convergence. Print the per-file status table and stop.
