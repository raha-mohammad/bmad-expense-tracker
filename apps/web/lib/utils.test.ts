import { describe, expect, it } from "vitest";

import { formatInrAmount } from "@/lib/utils";

describe("formatInrAmount", () => {
  it("formats a whole-rupee amount with no decimals", () => {
    expect(formatInrAmount("150")).toBe("₹150");
  });

  it("preserves paise when present", () => {
    expect(formatInrAmount("150.50")).toBe("₹150.50");
  });

  // The actual reason this function exists: Indian digit grouping (lakh/crore separators),
  // distinct from Western thousands grouping - untested by any small-amount fixture alone.
  it("applies Indian digit grouping for large amounts", () => {
    expect(formatInrAmount("150000")).toBe("₹1,50,000");
    expect(formatInrAmount("10000000")).toBe("₹1,00,00,000");
  });

  it("falls back to the raw string for a non-numeric input instead of rendering NaN", () => {
    expect(formatInrAmount("not-a-number")).toBe("not-a-number");
  });
});
