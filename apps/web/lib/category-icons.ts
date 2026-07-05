import {
  Circle,
  Film,
  HelpCircle,
  Receipt,
  ShoppingBag,
  Bus,
  Utensils,
  type LucideIcon,
} from "lucide-react";

// Placeholder mapping from the seeded icon keys (CategoryBootstrapRunner) to lucide-react icons.
// Not the final icon set - that's an explicitly deferred Epic 2 (Categories screen) decision.
// The fallback matters more than any individual mapping here.
const ICON_MAP: Record<string, LucideIcon> = {
  food: Utensils,
  transport: Bus,
  shopping: ShoppingBag,
  bills: Receipt,
  entertainment: Film,
  "help-circle": HelpCircle,
};

export function getCategoryIcon(iconKey: string): LucideIcon {
  return ICON_MAP[iconKey] ?? Circle;
}
