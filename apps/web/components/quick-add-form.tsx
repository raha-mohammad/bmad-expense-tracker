"use client";

import { useEffect, useRef, useState } from "react";
import { useRouter } from "next/navigation";

import { CategoryIconButton } from "@/components/category-icon-button";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { createTransaction, getCategories, getLastUsedCategory, type CategoryDto } from "@/lib/api";
import { cn } from "@/lib/utils";

const SAVE_HINT_ID = "quick-add-save-hint";

function formatDateLabel(dateIso: string, isToday: boolean): string {
  const formatted = new Date(`${dateIso}T00:00:00`).toLocaleDateString("en-IN", {
    day: "numeric",
    month: "short",
    year: "numeric",
  });
  return isToday ? `Today, ${formatted}` : formatted;
}

// Display-only "today" - used purely to render a friendly label and to open the date picker
// centered on today. AD-9 still holds for the data that's actually sent: transactionDate is
// omitted from the request unless the user explicitly picks a date (see handleSave). Derived
// from Asia/Kolkata (not the browser's local timezone) so the displayed/default date agrees
// with what the server would compute, per this story's own AD-9 hard constraint (code review).
function todayIso(): string {
  return new Intl.DateTimeFormat("en-CA", { timeZone: "Asia/Kolkata" }).format(new Date());
}

export function QuickAddForm() {
  const router = useRouter();
  const descriptionRef = useRef<HTMLInputElement>(null);
  // Synchronous in-flight guard: state-derived `canSave`/`isSaving` alone can't close a rapid
  // double-tap race (a second click can fire before React commits the isSaving=true render),
  // so this ref is checked and set immediately, before any state update (code review finding).
  const isSavingRef = useRef(false);

  const [categories, setCategories] = useState<CategoryDto[] | null>(null);
  const [loadError, setLoadError] = useState(false);

  const [amount, setAmount] = useState("");
  const [categoryId, setCategoryId] = useState<number | null>(null);
  const [description, setDescription] = useState("");
  // null = not yet touched by the user; the request omits transactionDate and the server
  // stamps "today" itself (AD-9). Only set once the user actually picks a date (backdating).
  const [pickedDate, setPickedDate] = useState<string | null>(null);

  const [isSaving, setIsSaving] = useState(false);
  const [saveError, setSaveError] = useState(false);

  useEffect(() => {
    let cancelled = false;

    // Categories and last-used-category are fetched independently: a failure in the narrow,
    // non-essential last-category lookup must not block the whole form from rendering (code
    // review finding) - it only affects which category, if any, is pre-selected.
    getCategories()
      .then((fetchedCategories) => {
        if (cancelled) return;
        setCategories(fetchedCategories);

        getLastUsedCategory()
          .then((lastUsed) => {
            if (cancelled) return;
            // First-ever-use edge case: no transaction has ever been logged, so there's no
            // last-used category - fall back to the first category rather than leaving Save
            // permanently gated on a selection no one ever made.
            setCategoryId(lastUsed.categoryId ?? fetchedCategories[0]?.id ?? null);
          })
          .catch(() => {
            if (!cancelled) setCategoryId(fetchedCategories[0]?.id ?? null);
          });
      })
      .catch(() => {
        if (!cancelled) setLoadError(true);
      });

    return () => {
      cancelled = true;
    };
  }, []);

  const amountValue = parseFloat(amount);
  const isAmountValid = !Number.isNaN(amountValue) && amountValue > 0;
  const isDescriptionValid = description.trim() !== "";
  const canSave = isAmountValid && categoryId != null && isDescriptionValid && !isSaving;

  const dateForDisplayAndPicker = pickedDate ?? todayIso();

  async function handleSave() {
    if (isSavingRef.current || !canSave) {
      // AC4: only the description case moves focus with an accessible required state - amount
      // and category gaps just no-op, matching epics.md exactly.
      if (!isDescriptionValid) {
        descriptionRef.current?.focus();
      }
      return;
    }

    isSavingRef.current = true;
    setIsSaving(true);
    setSaveError(false);
    try {
      await createTransaction({
        // Sent as a trimmed decimal string, not the parsed float - amountValue above exists
        // only for client-side validation (code review decision: money is string-typed end to
        // end, never a JS number, to avoid float precision drift).
        amount: amount.trim(),
        categoryId: categoryId as number,
        description: description.trim(),
        transactionDate: pickedDate ?? undefined,
      });
      router.push("/");
    } catch (err) {
      // Entered values are retained - no state is cleared here (AC6). Logged for diagnosability
      // so a real server rejection is distinguishable from an actual network failure (code
      // review finding) even though the displayed copy stays the same per AC6's exact wording.
      console.error("Failed to save transaction", err);
      setSaveError(true);
    } finally {
      isSavingRef.current = false;
      setIsSaving(false);
    }
  }

  return (
    <div className="mx-auto flex w-full max-w-md flex-1 flex-col gap-4 px-4 py-6">
      <h1 className="text-[1.0625rem] font-bold">Add Expense</h1>

      <div>
        <Label htmlFor="quick-add-amount">Amount</Label>
        <Input
          id="quick-add-amount"
          inputMode="decimal"
          placeholder="0"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          className="mt-1 h-auto border-none bg-transparent text-center text-[2.5rem] font-bold shadow-none focus-visible:ring-3 focus-visible:ring-ring/50 focus-visible:ring-offset-2"
        />
      </div>

      <div className="text-center">
        <label className="inline-flex flex-col items-center gap-1 text-xs text-muted-foreground">
          <span>{formatDateLabel(dateForDisplayAndPicker, pickedDate == null)}</span>
          <input
            type="date"
            value={dateForDisplayAndPicker}
            onChange={(e) => setPickedDate(e.target.value || null)}
            className="rounded-sm border border-border bg-transparent px-2 py-1 text-sm outline-none focus-visible:ring-3 focus-visible:ring-ring/50 focus-visible:ring-offset-2"
          />
        </label>
      </div>

      <div>
        <span className="mb-2 block text-xs font-bold tracking-wide text-muted-foreground uppercase">Category</span>
        {loadError && <p className="text-sm text-destructive">Couldn&apos;t load categories — check your connection.</p>}
        {!loadError && categories == null && <p className="text-sm text-muted-foreground">Loading categories…</p>}
        {categories != null && (
          <div className="flex gap-2 overflow-x-auto pb-1">
            {categories.map((category) => (
              <CategoryIconButton
                key={category.id}
                name={category.name}
                icon={category.icon}
                selected={category.id === categoryId}
                onSelect={() => setCategoryId(category.id)}
              />
            ))}
          </div>
        )}
      </div>

      <div>
        <Label htmlFor="quick-add-description">Description</Label>
        <Input
          id="quick-add-description"
          ref={descriptionRef}
          placeholder="e.g. Coffee with friends"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          className="mt-1"
        />
        {!isDescriptionValid && (
          <span id={SAVE_HINT_ID} className="mt-1 block text-xs text-muted-foreground">
            Add a description to save
          </span>
        )}
      </div>

      <div aria-live="polite">
        {saveError && <p className="text-sm text-destructive">Couldn&apos;t save — check your connection.</p>}
      </div>

      <div className="mt-2 flex justify-end">
        {/* aria-disabled (not the native `disabled` attribute) is deliberate: a natively
            disabled button can't receive the tap AC4 requires to move focus into the
            description field, so visual-only disabling + aria-disabled + handleSave's own
            guard is the correct accessible pattern here - pointer-events must stay enabled. */}
        <Button
          type="button"
          aria-label="Save expense"
          aria-disabled={!canSave}
          aria-describedby={!isDescriptionValid ? SAVE_HINT_ID : undefined}
          onClick={handleSave}
          size="icon-lg"
          className={cn(
            "rounded-full",
            // DESIGN.md's quick-add-save-button spec: disabled is a real, distinct color pair
            // (#E5E7EB/#9CA3AF), not just a low-opacity primary - low-opacity was tried first
            // here and didn't read as clearly disabled once actually rendered.
            !canSave && "bg-[#E5E7EB] text-[#9CA3AF] hover:bg-[#E5E7EB]"
          )}
        >
          ✓
        </Button>
      </div>
    </div>
  );
}
