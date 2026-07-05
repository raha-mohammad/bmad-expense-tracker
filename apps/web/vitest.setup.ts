import "@testing-library/jest-dom/vitest";
import { cleanup } from "@testing-library/react";
import { afterEach } from "vitest";

// @testing-library/react's own auto-cleanup only registers itself when it detects a global
// `afterEach` (e.g. Vitest's `globals: true`). This project imports test APIs explicitly
// instead, so cleanup must be wired up here or every test file leaks its DOM into the next.
afterEach(() => {
  cleanup();
});
