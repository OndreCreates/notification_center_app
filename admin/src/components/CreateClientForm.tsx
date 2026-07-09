"use client";

import { useState, useTransition } from "react";
import { createClientAction } from "@/lib/actions";
import type { CreateClientResult } from "@/lib/types";

export function CreateClientForm() {
  const [isPending, startTransition] = useTransition();
  const [error, setError] = useState<string | null>(null);
  const [created, setCreated] = useState<CreateClientResult | null>(null);

  function handleSubmit(formData: FormData) {
    setError(null);
    setCreated(null);

    const name = String(formData.get("name") ?? "");
    const contactEmail = String(formData.get("contactEmail") ?? "");

    startTransition(async () => {
      try {
        const result = await createClientAction(name, contactEmail);
        setCreated(result);
      } catch (e) {
        setError(e instanceof Error ? e.message : "Nepodařilo se vytvořit klienta.");
      }
    });
  }

  return (
    <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
      <h2 className="text-sm font-medium text-slate-500">Nový klient</h2>

      <form action={handleSubmit} className="mt-3 flex flex-wrap items-end gap-3">
        <div className="flex flex-col gap-1">
          <label className="text-xs text-slate-500" htmlFor="name">
            Název
          </label>
          <input
            id="name"
            name="name"
            required
            className="rounded-lg border border-slate-200 px-3 py-2 text-sm focus:border-slate-400 focus:outline-none"
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-xs text-slate-500" htmlFor="contactEmail">
            Kontaktní e-mail (nepovinné)
          </label>
          <input
            id="contactEmail"
            name="contactEmail"
            type="email"
            className="rounded-lg border border-slate-200 px-3 py-2 text-sm focus:border-slate-400 focus:outline-none"
          />
        </div>
        <button
          type="submit"
          disabled={isPending}
          className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-700 disabled:opacity-50"
        >
          {isPending ? "Vytvářím…" : "Vytvořit klienta"}
        </button>
      </form>

      {error && <p className="mt-3 text-sm text-red-600">{error}</p>}

      {created && (
        <div className="mt-4 rounded-lg border border-amber-200 bg-amber-50 p-4">
          <p className="text-sm font-medium text-amber-900">
            Klient &ldquo;{created.name}&rdquo; vytvořen. API klíč se zobrazí jen teď — ulož si ho:
          </p>
          <code className="mt-2 block break-all rounded bg-white px-3 py-2 text-sm text-slate-900 ring-1 ring-amber-200">
            {created.apiKey}
          </code>
        </div>
      )}
    </div>
  );
}
