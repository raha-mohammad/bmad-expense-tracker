"use client";

import type { CategoryDto, FrequentExpenseDto } from "@/lib/api";
import { getCategoryIcon } from "@/lib/category-icons";
import { cn, formatInrAmount } from "@/lib/utils";

interface FrequentExpenseShelfProps {
  chips: FrequentExpenseDto[];
  categories: CategoryDto[];
  disabled: boolean;
  onTap: (chip: FrequentExpenseDto) => void;
}

// FR-1/UX-DR7: pill-shaped, horizontally-scrollable shelf, visually distinct from the
// rounded-square CategoryIconButton so the two fast paths never look identical.
export function FrequentExpenseShelf({ chips, categories, disabled, onTap }: FrequentExpenseShelfProps) {
  const resolved = chips
    .map((chip) => ({ chip, category: categories.find((c) => c.id === chip.categoryId) }))
    .filter((entry): entry is { chip: FrequentExpenseDto; category: CategoryDto } => entry.category != null);

  // AC6: hidden entirely (not an empty/placeholder state) when nothing resolves - first-ever
  // use, no purchase has repeated yet, or the fetch failed.
  if (resolved.length === 0) return null;

  return (
    <div>
      <span className="mb-2 block text-xs font-bold tracking-wide text-muted-foreground uppercase">
        Frequent — tap to log instantly
      </span>
      <div className="flex gap-2 overflow-x-auto pb-1">
        {resolved.map(({ chip, category }) => {
          const Icon = getCategoryIcon(category.icon);
          const key = `${chip.categoryId}-${chip.amount}-${chip.description}`;
          return (
            <button
              key={key}
              type="button"
              disabled={disabled}
              onClick={() => onTap(chip)}
              className={cn(
                "flex shrink-0 items-center gap-1.5 whitespace-nowrap rounded-full bg-chip-bg px-3 py-2 text-sm text-card-foreground outline-none focus-visible:ring-3 focus-visible:ring-ring/50 focus-visible:ring-offset-2",
                // Shared disabled-color token (globals.css) - a real distinct color pair, not
                // just low-opacity, per DESIGN.md; also used by quick-add-form.tsx's Save button
                // (code review finding: extracted to a shared token instead of duplicated hex).
                disabled && "bg-disabled text-disabled-foreground"
              )}
            >
              <Icon aria-hidden="true" className="size-4" />
              <span>
                {chip.description} · {formatInrAmount(chip.amount)}
              </span>
            </button>
          );
        })}
      </div>
    </div>
  );
}
