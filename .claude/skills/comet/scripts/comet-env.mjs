#!/usr/bin/env node
// Comet script locator — prints the absolute path to this scripts directory.
//
// Usage:
//   COMET_SCRIPTS_DIR="$(node /path/to/comet-env.mjs)"
//
// The skill boilerplate runs this once to resolve the sibling command scripts
// (comet-state.mjs, comet-guard.mjs, ...) without depending on bash.
import { dirname } from 'node:path';
import { fileURLToPath } from 'node:url';

// Use forward slashes so the path is safe to interpolate into any shell and
// is accepted verbatim by Node on every platform (Windows included).
const scriptDir = dirname(fileURLToPath(import.meta.url)).replace(/\\/gu, '/');
process.stdout.write(`${scriptDir}\n`);
