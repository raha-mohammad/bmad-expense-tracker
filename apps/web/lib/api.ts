const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

export type CategoryKind = "DEFAULT" | "CUSTOM" | "SYSTEM";

export interface CategoryDto {
  id: number;
  name: string;
  icon: string;
  kind: CategoryKind;
}

export interface LastUsedCategoryDto {
  categoryId: number | null;
}

export interface CreateTransactionPayload {
  // A decimal string (e.g. "150.00"), never a JS number - code review decision (Story 1.2):
  // money travels as a string end-to-end so a JS number's float representation never has a
  // chance to introduce precision drift. The server's TransactionDto mirrors this on the way out.
  amount: string;
  categoryId: number;
  description: string;
  // ISO yyyy-MM-dd; omit to let the server default to "today" in Asia/Kolkata (AD-9).
  transactionDate?: string;
}

export interface TransactionDto {
  id: number;
  categoryId: number;
  amount: string;
  transactionDate: string;
  description: string;
}

interface ErrorResponseBody {
  error?: { code: string; message: string };
}

// Never swallows a failure: a non-2xx response throws, and fetch itself already rejects on a
// network error - both paths reach the caller so Quick Add's save-failure state (AC6) can react.
// When the server responds with the structured {error:{code,message}} shape (AD-8), that message
// is used instead of a generic status-code string, so a real validation/not-found failure is
// distinguishable from an actual network error by whoever logs or inspects the thrown Error.
async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...init,
  });

  if (!response.ok) {
    const body = (await response.json().catch(() => null)) as ErrorResponseBody | null;
    throw new Error(body?.error?.message ?? `Request to ${path} failed with status ${response.status}`);
  }

  return response.json() as Promise<T>;
}

export function getCategories(): Promise<CategoryDto[]> {
  return request<CategoryDto[]>("/api/categories");
}

export function getLastUsedCategory(): Promise<LastUsedCategoryDto> {
  return request<LastUsedCategoryDto>("/api/transactions/last-category");
}

export function createTransaction(payload: CreateTransactionPayload): Promise<TransactionDto> {
  return request<TransactionDto>("/api/transactions", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}
