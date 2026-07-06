import { render, screen } from "@testing-library/react";
import { fireEvent } from "@testing-library/dom";
import { describe, expect, it, vi } from "vitest";

import { FrequentExpenseShelf } from "@/components/frequent-expense-shelf";
import type { CategoryDto, FrequentExpenseDto } from "@/lib/api";

const CATEGORIES: CategoryDto[] = [
  { id: 1, name: "Food", icon: "food", kind: "DEFAULT" },
  { id: 2, name: "Transport", icon: "transport", kind: "DEFAULT" },
];

const COFFEE_CHIP: FrequentExpenseDto = { categoryId: 1, amount: "150", description: "Coffee" };
const BUS_CHIP: FrequentExpenseDto = { categoryId: 2, amount: "40", description: "Bus" };

describe("FrequentExpenseShelf", () => {
  // AC6: hidden entirely, not an empty/placeholder state.
  it("renders nothing when there are no chips", () => {
    const { container } = render(
      <FrequentExpenseShelf chips={[]} categories={CATEGORIES} disabled={false} onTap={vi.fn()} />
    );

    expect(container).toBeEmptyDOMElement();
  });

  it("renders one button per chip with its description and formatted amount", () => {
    render(
      <FrequentExpenseShelf chips={[COFFEE_CHIP, BUS_CHIP]} categories={CATEGORIES} disabled={false} onTap={vi.fn()} />
    );

    expect(screen.getByRole("button", { name: /Coffee.*₹150/ })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /Bus.*₹40/ })).toBeInTheDocument();
  });

  // Defensive: a chip referencing a categoryId absent from the loaded categories list (not
  // reachable today - category deletion doesn't exist until Epic 2) is silently omitted.
  it("omits a chip whose categoryId isn't found in categories", () => {
    const orphanChip: FrequentExpenseDto = { categoryId: 999, amount: "10", description: "Mystery" };
    render(<FrequentExpenseShelf chips={[orphanChip]} categories={CATEGORIES} disabled={false} onTap={vi.fn()} />);

    expect(screen.queryByRole("button", { name: /Mystery/ })).not.toBeInTheDocument();
  });

  it("calls onTap with the exact chip when an enabled chip is tapped", () => {
    const onTap = vi.fn();
    render(<FrequentExpenseShelf chips={[COFFEE_CHIP]} categories={CATEGORIES} disabled={false} onTap={onTap} />);

    fireEvent.click(screen.getByRole("button", { name: /Coffee/ }));

    expect(onTap).toHaveBeenCalledWith(COFFEE_CHIP);
  });

  // AC7: disabled chips are native-disabled and don't fire onTap.
  it("renders disabled chips that don't fire onTap when clicked", () => {
    const onTap = vi.fn();
    render(<FrequentExpenseShelf chips={[COFFEE_CHIP]} categories={CATEGORIES} disabled={true} onTap={onTap} />);

    const chip = screen.getByRole("button", { name: /Coffee/ });
    expect(chip).toBeDisabled();

    fireEvent.click(chip);
    expect(onTap).not.toHaveBeenCalled();
  });
});
