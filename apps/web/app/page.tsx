import Link from "next/link";

import { buttonVariants } from "@/components/ui/button";

// Temporary entry point - there is no real Dashboard yet (Story 1.4 replaces this placeholder).
// A plain styled <Link> (not the Button component's `render` composition) so this stays a real
// <a> with its native "link" role intact - it's a navigation, not an in-place action, and
// Button's polymorphic `render` would otherwise override the accessible role to "button".
export default function Home() {
  return (
    <div className="flex flex-1 flex-col items-center justify-center gap-4 px-6 text-center font-sans">
      <h1 className="text-2xl font-semibold">bmad Expense Tracker</h1>
      <p className="text-muted-foreground">Scaffolding in progress.</p>
      <Link href="/quick-add" className={buttonVariants()}>
        Add Expense
      </Link>
    </div>
  );
}
