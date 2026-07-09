"use client";

import { useRouter, useSearchParams } from "next/navigation";
import type { ClientSummary } from "@/lib/types";

export function FilterBar({ clients }: { clients: ClientSummary[] }) {
  const router = useRouter();
  const searchParams = useSearchParams();

  function updateParam(key: string, value: string) {
    const params = new URLSearchParams(searchParams.toString());
    if (value) {
      params.set(key, value);
    } else {
      params.delete(key);
    }
    params.delete("page");
    router.push(`/?${params.toString()}`);
  }

  return (
    <div className="flex flex-wrap gap-3">
      <select
        className="rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 shadow-sm focus:border-slate-400 focus:outline-none"
        defaultValue={searchParams.get("status") ?? ""}
        onChange={(e) => updateParam("status", e.target.value)}
      >
        <option value="">Všechny stavy</option>
        <option value="PENDING">PENDING</option>
        <option value="SENT">SENT</option>
        <option value="DEAD">DEAD</option>
      </select>

      <select
        className="rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 shadow-sm focus:border-slate-400 focus:outline-none"
        defaultValue={searchParams.get("channel") ?? ""}
        onChange={(e) => updateParam("channel", e.target.value)}
      >
        <option value="">Všechny kanály</option>
        <option value="EMAIL">E-mail</option>
        <option value="WEBSOCKET">In-app</option>
      </select>

      <select
        className="rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 shadow-sm focus:border-slate-400 focus:outline-none"
        defaultValue={searchParams.get("clientId") ?? ""}
        onChange={(e) => updateParam("clientId", e.target.value)}
      >
        <option value="">Všichni klienti</option>
        {clients.map((client) => (
          <option key={client.id} value={client.id}>
            {client.name}
          </option>
        ))}
      </select>
    </div>
  );
}
