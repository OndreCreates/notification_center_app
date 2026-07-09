"use client";

import { useState, useTransition } from "react";
import { createTemplateAction } from "@/lib/actions";
import type { NotificationChannel } from "@/lib/types";

export function CreateTemplateForm() {
  const [isPending, startTransition] = useTransition();
  const [error, setError] = useState<string | null>(null);

  function handleSubmit(formData: FormData) {
    setError(null);

    const code = String(formData.get("code") ?? "");
    const channel = String(formData.get("channel") ?? "EMAIL") as NotificationChannel;
    const content = String(formData.get("content") ?? "");

    startTransition(async () => {
      try {
        await createTemplateAction(code, channel, content);
        (document.getElementById("create-template-form") as HTMLFormElement)?.reset();
      } catch (e) {
        setError(e instanceof Error ? e.message : "Nepodařilo se vytvořit šablonu.");
      }
    });
  }

  return (
    <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
      <h2 className="text-sm font-medium text-slate-500">Nová šablona</h2>

      <form id="create-template-form" action={handleSubmit} className="mt-3 flex flex-col gap-3">
        <div className="flex flex-wrap gap-3">
          <div className="flex flex-col gap-1">
            <label className="text-xs text-slate-500" htmlFor="code">
              Kód
            </label>
            <input
              id="code"
              name="code"
              required
              placeholder="welcome"
              className="rounded-lg border border-slate-200 px-3 py-2 text-sm focus:border-slate-400 focus:outline-none"
            />
          </div>
          <div className="flex flex-col gap-1">
            <label className="text-xs text-slate-500" htmlFor="channel">
              Kanál
            </label>
            <select
              id="channel"
              name="channel"
              className="rounded-lg border border-slate-200 px-3 py-2 text-sm focus:border-slate-400 focus:outline-none"
            >
              <option value="EMAIL">E-mail</option>
              <option value="WEBSOCKET">In-app</option>
            </select>
          </div>
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-xs text-slate-500" htmlFor="content">
            Obsah (Thymeleaf HTML, placeholdery přes ${"{"}promenna{"}"})
          </label>
          <textarea
            id="content"
            name="content"
            required
            rows={4}
            placeholder={'<p th:text="${message}">…</p>'}
            className="rounded-lg border border-slate-200 px-3 py-2 font-mono text-sm focus:border-slate-400 focus:outline-none"
          />
        </div>

        <button
          type="submit"
          disabled={isPending}
          className="self-start rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-700 disabled:opacity-50"
        >
          {isPending ? "Vytvářím…" : "Vytvořit šablonu"}
        </button>
      </form>

      {error && <p className="mt-3 text-sm text-red-600">{error}</p>}
    </div>
  );
}
