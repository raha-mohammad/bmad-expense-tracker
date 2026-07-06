import { render, screen, waitFor } from "@testing-library/react";
import { fireEvent } from "@testing-library/dom";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { QuickAddForm } from "@/components/quick-add-form";

const pushMock = vi.fn();

vi.mock("next/navigation", () => ({
  useRouter: () => ({ push: pushMock }),
}));

const getCategoriesMock = vi.fn();
const getLastUsedCategoryMock = vi.fn();
const getFrequentExpensesMock = vi.fn();
const createTransactionMock = vi.fn();

vi.mock("@/lib/api", () => ({
  getCategories: (...args: unknown[]) => getCategoriesMock(...args),
  getLastUsedCategory: (...args: unknown[]) => getLastUsedCategoryMock(...args),
  getFrequentExpenses: (...args: unknown[]) => getFrequentExpensesMock(...args),
  createTransaction: (...args: unknown[]) => createTransactionMock(...args),
}));

const CATEGORIES = [
  { id: 1, name: "Food", icon: "food", kind: "DEFAULT" as const },
  { id: 2, name: "Transport", icon: "transport", kind: "DEFAULT" as const },
];

beforeEach(() => {
  pushMock.mockReset();
  getCategoriesMock.mockReset().mockResolvedValue(CATEGORIES);
  getLastUsedCategoryMock.mockReset().mockResolvedValue({ categoryId: 1 });
  getFrequentExpensesMock.mockReset().mockResolvedValue([]);
  createTransactionMock.mockReset();
});

async function renderForm() {
  render(<QuickAddForm />);
  // Wait for the mocked category fetch to resolve before each test interacts with the form.
  await screen.findByText("Food");
}

function saveButton() {
  return screen.getByRole("button", { name: "Save expense" });
}

describe("QuickAddForm", () => {
  it("disables Save and exposes the description hint when description is empty", async () => {
    await renderForm();

    expect(saveButton()).toHaveAttribute("aria-disabled", "true");
    expect(saveButton()).toHaveAttribute("aria-describedby", "quick-add-save-hint");
    expect(screen.getByText("Add a description to save")).toBeInTheDocument();
  });

  it("keeps Save disabled when amount is filled but description is still empty", async () => {
    await renderForm();

    fireEvent.change(screen.getByLabelText("Amount"), { target: { value: "150" } });

    expect(saveButton()).toHaveAttribute("aria-disabled", "true");
  });

  it("enables Save once amount, category, and description are all filled", async () => {
    await renderForm();

    fireEvent.change(screen.getByLabelText("Amount"), { target: { value: "150" } });
    fireEvent.change(screen.getByLabelText("Description"), { target: { value: "Coffee" } });

    expect(saveButton()).toHaveAttribute("aria-disabled", "false");
  });

  it("moves focus to the description field when Save is tapped while it's empty", async () => {
    await renderForm();

    fireEvent.change(screen.getByLabelText("Amount"), { target: { value: "150" } });
    fireEvent.click(saveButton());

    expect(screen.getByLabelText("Description")).toHaveFocus();
    expect(createTransactionMock).not.toHaveBeenCalled();
  });

  it("tapping a category button selects it and deselects the previous one", async () => {
    await renderForm();

    const foodButton = screen.getByRole("button", { name: /Food/ });
    const transportButton = screen.getByRole("button", { name: /Transport/ });

    expect(foodButton).toHaveAttribute("aria-pressed", "true");
    expect(transportButton).toHaveAttribute("aria-pressed", "false");

    fireEvent.click(transportButton);

    expect(transportButton).toHaveAttribute("aria-pressed", "true");
    expect(foodButton).toHaveAttribute("aria-pressed", "false");
  });

  it("navigates to / after a successful save, sending amount as a string", async () => {
    createTransactionMock.mockResolvedValue({
      id: 1,
      categoryId: 1,
      amount: "150",
      transactionDate: "2026-07-05",
      description: "Coffee",
    });
    await renderForm();

    fireEvent.change(screen.getByLabelText("Amount"), { target: { value: "150" } });
    fireEvent.change(screen.getByLabelText("Description"), { target: { value: "Coffee" } });
    fireEvent.click(saveButton());

    await waitFor(() => expect(pushMock).toHaveBeenCalledWith("/"));
    // Money travels as a decimal string end to end (code review decision), never a JS number.
    expect(createTransactionMock).toHaveBeenCalledWith(expect.objectContaining({ amount: "150" }));
  });

  it("double-clicking Save before the request resolves creates only one transaction", async () => {
    let resolveCreate: (value: unknown) => void = () => {};
    createTransactionMock.mockReturnValue(
      new Promise((resolve) => {
        resolveCreate = resolve;
      })
    );
    await renderForm();

    fireEvent.change(screen.getByLabelText("Amount"), { target: { value: "150" } });
    fireEvent.change(screen.getByLabelText("Description"), { target: { value: "Coffee" } });

    fireEvent.click(saveButton());
    fireEvent.click(saveButton());

    resolveCreate({ id: 1, categoryId: 1, amount: "150", transactionDate: "2026-07-05", description: "Coffee" });
    await waitFor(() => expect(pushMock).toHaveBeenCalledWith("/"));

    expect(createTransactionMock).toHaveBeenCalledTimes(1);
  });

  it("sends no transactionDate when the date field is cleared back to empty", async () => {
    createTransactionMock.mockResolvedValue({
      id: 1,
      categoryId: 1,
      amount: "150",
      transactionDate: "2026-07-05",
      description: "Coffee",
    });
    const { container } = render(<QuickAddForm />);
    await screen.findByText("Food");

    const dateInput = container.querySelector('input[type="date"]') as HTMLInputElement;
    fireEvent.change(dateInput, { target: { value: "2026-07-01" } });
    fireEvent.change(dateInput, { target: { value: "" } });

    fireEvent.change(screen.getByLabelText("Amount"), { target: { value: "150" } });
    fireEvent.change(screen.getByLabelText("Description"), { target: { value: "Coffee" } });
    fireEvent.click(saveButton());

    await waitFor(() =>
      expect(createTransactionMock).toHaveBeenCalledWith(expect.objectContaining({ transactionDate: undefined }))
    );
  });

  it("keeps entered values and shows an inline aria-live error when save fails", async () => {
    createTransactionMock.mockRejectedValue(new Error("network error"));
    await renderForm();

    fireEvent.change(screen.getByLabelText("Amount"), { target: { value: "150" } });
    fireEvent.change(screen.getByLabelText("Description"), { target: { value: "Coffee" } });
    fireEvent.click(saveButton());

    await screen.findByText("Couldn't save — check your connection.");
    expect(screen.getByLabelText("Amount")).toHaveValue("150");
    expect(screen.getByLabelText("Description")).toHaveValue("Coffee");
    expect(pushMock).not.toHaveBeenCalled();
  });

  // AC6: no shelf section at all when there are no frequent-expense chips yet.
  it("does not render the frequent-expenses shelf when none exist", async () => {
    await renderForm();

    expect(screen.queryByText("Frequent — tap to log instantly")).not.toBeInTheDocument();
  });

  // AC2/AC3: tapping a chip creates a transaction with its exact preset values and navigates.
  it("tapping a frequent-expense chip creates a transaction and navigates to /", async () => {
    getFrequentExpensesMock.mockResolvedValue([{ categoryId: 2, amount: "150", description: "Coffee" }]);
    createTransactionMock.mockResolvedValue({
      id: 1,
      categoryId: 2,
      amount: "150",
      transactionDate: "2026-07-05",
      description: "Coffee",
    });
    await renderForm();

    fireEvent.click(await screen.findByRole("button", { name: /Coffee/ }));

    await waitFor(() => expect(pushMock).toHaveBeenCalledWith("/"));
    expect(createTransactionMock).toHaveBeenCalledWith({
      amount: "150",
      categoryId: 2,
      description: "Coffee",
      transactionDate: undefined,
    });
  });

  // AC7: a chip tap while a save is already in flight (chip or manual) is a no-op - modeled
  // directly on the equivalent double-click-Save guard test above.
  it("a second tap while a chip-tap request is pending is a no-op", async () => {
    getFrequentExpensesMock.mockResolvedValue([{ categoryId: 2, amount: "150", description: "Coffee" }]);
    let resolveCreate: (value: unknown) => void = () => {};
    createTransactionMock.mockReturnValue(
      new Promise((resolve) => {
        resolveCreate = resolve;
      })
    );
    await renderForm();

    const chip = await screen.findByRole("button", { name: /Coffee/ });
    fireEvent.click(chip);
    fireEvent.click(chip);

    resolveCreate({ id: 1, categoryId: 2, amount: "150", transactionDate: "2026-07-05", description: "Coffee" });
    await waitFor(() => expect(pushMock).toHaveBeenCalledWith("/"));

    expect(createTransactionMock).toHaveBeenCalledTimes(1);
  });

  // AC7 (Task 9's own literal scope): a DIFFERENT chip tapped while a chip-tap is already in
  // flight is also a no-op - the shared isSavingRef guard blocks any second save, not just a
  // retap of the same chip.
  it("tapping a different chip while a chip-tap request is pending is a no-op", async () => {
    getFrequentExpensesMock.mockResolvedValue([
      { categoryId: 1, amount: "150", description: "Coffee" },
      { categoryId: 2, amount: "40", description: "Bus" },
    ]);
    let resolveCreate: (value: unknown) => void = () => {};
    createTransactionMock.mockReturnValue(
      new Promise((resolve) => {
        resolveCreate = resolve;
      })
    );
    await renderForm();

    fireEvent.click(await screen.findByRole("button", { name: /Coffee/ }));
    fireEvent.click(screen.getByRole("button", { name: /Bus/ }));

    resolveCreate({ id: 1, categoryId: 1, amount: "150", transactionDate: "2026-07-05", description: "Coffee" });
    await waitFor(() => expect(pushMock).toHaveBeenCalledWith("/"));

    expect(createTransactionMock).toHaveBeenCalledTimes(1);
  });

  // AC7 (Task 9's own literal scope): tapping manual Save while a chip-tap is already in flight
  // is also a no-op - the two save paths share one in-flight guard.
  it("tapping manual Save while a chip-tap request is pending is a no-op", async () => {
    getFrequentExpensesMock.mockResolvedValue([{ categoryId: 1, amount: "150", description: "Coffee" }]);
    let resolveCreate: (value: unknown) => void = () => {};
    createTransactionMock.mockReturnValue(
      new Promise((resolve) => {
        resolveCreate = resolve;
      })
    );
    await renderForm();

    fireEvent.change(screen.getByLabelText("Amount"), { target: { value: "150" } });
    fireEvent.change(screen.getByLabelText("Description"), { target: { value: "Coffee" } });

    fireEvent.click(await screen.findByRole("button", { name: /Coffee/ }));
    fireEvent.click(saveButton());

    resolveCreate({ id: 1, categoryId: 1, amount: "150", transactionDate: "2026-07-05", description: "Coffee" });
    await waitFor(() => expect(pushMock).toHaveBeenCalledWith("/"));

    expect(createTransactionMock).toHaveBeenCalledTimes(1);
  });
});
