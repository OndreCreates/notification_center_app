"use client";

import { useTransition } from "react";
import { setClientActiveAction } from "@/lib/actions";
import type { ClientSummary } from "@/lib/types";

export function ClientTable({ clients }: { clients: ClientSummary[] }) {
  const [isPending, startTransition] = useTransition();

  function toggle(client: ClientSummary) {
    startTransition(async () => {
      await setClientActiveAction(client.id, !client.active);
    });
  }

  return (
    <div className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
      <table className="min-w-full divide-y divide-slate-200 text-sm">
        <thead className="bg-slate-50">
          <tr className="text-left text-xs font-medium uppercase tracking-wide text-slate-500">
            <th className="px-4 py-3">ID</th>
            <th className="px-4 py-3">Název</th>
            <th className="px-4 py-3">Stav</th>
            <th className="px-4 py-3" />
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-100">
          {clients.map((client) => (
            <tr key={client.id}>
              <td className="px-4 py-3 text-slate-500">{client.id}</td>
              <td className="px-4 py-3 font-medium text-slate-900">{client.name}</td>
              <td className="px-4 py-3">
                <span
                  className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ring-1 ring-inset ${
                    client.active
                      ? "bg-emerald-50 text-emerald-700 ring-emerald-600/20"
                      : "bg-slate-100 text-slate-500 ring-slate-500/10"
                  }`}
                >
                  {client.active ? "AKTIVNÍ" : "NEAKTIVNÍ"}
                </span>
              </td>
              <td className="px-4 py-3 text-right">
                <button
                  onClick={() => toggle(client)}
                  disabled={isPending}
                  className="rounded-lg border border-slate-200 px-3 py-1.5 text-xs font-medium text-slate-700 hover:bg-slate-50 disabled:opacity-50"
                >
                  {client.active ? "Deaktivovat" : "Aktivovat"}
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
