import { clsx, type ClassValue } from "clsx"
import { twMerge } from "tailwind-merge"

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

// Indian digit grouping (e.g. "1,00,000"), no decimals for whole-rupee amounts (matches the
// product's example copy, e.g. "₹150") but still shows paise when present (e.g. "₹150.50").
// First consumer: the Frequent-Expenses Shelf (Story 1.3) - Dashboard totals (1.4), Budget Status
// (1.6), and the Search & Filter running total (3.1) should reuse this, not reinvent it.
export function formatInrAmount(amount: string): string {
  const value = Number(amount);
  // Defensive: this is a shared utility future stories (1.4/1.6/3.1) will call with data we
  // can't fully audit yet, unlike today's only caller (a validated NUMERIC(12,2) from the
  // database) - a malformed input renders the raw string instead of "₹NaN" (code review finding).
  if (Number.isNaN(value)) return amount;
  return new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: Number.isInteger(value) ? 0 : 2,
  }).format(value);
}
