"use client";

import { Check } from "lucide-react";

import { getCategoryIcon } from "@/lib/category-icons";
import { cn } from "@/lib/utils";

interface CategoryIconButtonProps {
  name: string;
  icon: string;
  selected: boolean;
  onSelect: () => void;
}

// UX-DR6: ≥60x60px, never a dropdown, selected state pairs border+fill+checkmark badge with
// aria-pressed so selection never depends on the border-color change alone.
export function CategoryIconButton({ name, icon, selected, onSelect }: CategoryIconButtonProps) {
  const Icon = getCategoryIcon(icon);

  return (
    <button
      type="button"
      aria-pressed={selected}
      onClick={onSelect}
      className={cn(
        "relative flex min-h-[60px] min-w-[60px] shrink-0 flex-col items-center justify-center gap-1 rounded-md border-2 border-transparent bg-card px-1 py-2 text-card-foreground outline-none transition-colors focus-visible:ring-3 focus-visible:ring-ring/50 focus-visible:ring-offset-2",
        selected && "border-primary bg-category-selected-bg"
      )}
    >
      {/* eslint-disable-next-line react-hooks/static-components -- getCategoryIcon returns a
          stable reference from a fixed module-level map, not a component created per render */}
      <Icon aria-hidden="true" className="size-5" />
      <span className="text-[9px] font-bold">{name}</span>
      {selected && (
        <span
          aria-hidden="true"
          className="absolute -top-1.5 -right-1.5 flex size-4 items-center justify-center rounded-full bg-primary text-primary-foreground"
        >
          <Check className="size-3" />
        </span>
      )}
    </button>
  );
}
