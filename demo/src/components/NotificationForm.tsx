"use client";

import { useState, useTransition } from "react";
import { sendNotificationAction, type SendNotificationInput } from "@/lib/actions";

export function NotificationForm() {
  const [isPending, startTransition] = useTransition();
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  function handleSubmit(formData: FormData) {
    setError(null);
    setSuccess(null);

    const input: SendNotificationInput = {
      channel: (formData.get("channel") as "EMAIL" | "WEBSOCKET") ?? "WEBSOCKET",
      recipient: String(formData.get("recipient") ?? ""),
      subject: String(formData.get("subject") ?? ""),
      body: String(formData.get("body") ?? ""),
    };

    startTransition(async () => {
      try {
        const result = await sendNotificationAction(input);
        setSuccess(`Notifikace #${result.id} odeslána (stav: ${result.status}).`);
        (document.getElementById("notification-form") as HTMLFormElement)?.reset();
      } catch (e) {
        setError(e instanceof Error ? e.message : "Odeslání selhalo.");
      }
    });
  }

  return (
    <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
      <h2 className="text-sm font-medium text-slate-500">Poslat notifikaci</h2>

      <form id="notification-form" action={handleSubmit} className="mt-3 flex flex-col gap-3">
        <div className="flex flex-col gap-1">
          <label className="text-xs text-slate-500" htmlFor="channel">
            Kanál
          </label>
          <select
            id="channel"
            name="channel"
            defaultValue="WEBSOCKET"
            className="rounded-lg border border-slate-200 px-3 py-2 text-sm focus:border-slate-400 focus:outline-none"
          >
            <option value="WEBSOCKET">In-app (live feed vpravo)</option>
            <option value="EMAIL">E-mail (Mailhog)</option>
          </select>
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-xs text-slate-500" htmlFor="recipient">
            Příjemce
          </label>
          <input
            id="recipient"
            name="recipient"
            required
            defaultValue="demo@example.com"
            className="rounded-lg border border-slate-200 px-3 py-2 text-sm focus:border-slate-400 focus:outline-none"
          />
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-xs text-slate-500" htmlFor="subject">
            Předmět
          </label>
          <input
            id="subject"
            name="subject"
            required
            defaultValue="Ahoj z demo appky"
            className="rounded-lg border border-slate-200 px-3 py-2 text-sm focus:border-slate-400 focus:outline-none"
          />
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-xs text-slate-500" htmlFor="body">
            Zpráva
          </label>
          <textarea
            id="body"
            name="body"
            required
            rows={3}
            defaultValue="Tahle notifikace přišla přes Notification Center API."
            className="rounded-lg border border-slate-200 px-3 py-2 text-sm focus:border-slate-400 focus:outline-none"
          />
        </div>

        <button
          type="submit"
          disabled={isPending}
          className="self-start rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-700 disabled:opacity-50"
        >
          {isPending ? "Odesílám…" : "Odeslat notifikaci"}
        </button>
      </form>

      {error && <p className="mt-3 text-sm text-red-600">{error}</p>}
      {success && <p className="mt-3 text-sm text-emerald-700">{success}</p>}
    </div>
  );
}
