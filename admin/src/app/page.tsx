import Link from "next/link";
import { listClients, listNotifications } from "@/lib/api";
import { formatDate } from "@/lib/format";
import { StatusBadge } from "@/components/StatusBadge";
import { ChannelBadge } from "@/components/ChannelBadge";
import { FilterBar } from "@/components/FilterBar";

export default async function NotificationsPage({
  searchParams,
}: {
  searchParams: Promise<{ [key: string]: string | undefined }>;
}) {
  const params = await searchParams;
  const page = params.page ? Number(params.page) : 0;

  function pageHref(targetPage: number) {
    const next = new URLSearchParams();
    if (params.status) next.set("status", params.status);
    if (params.channel) next.set("channel", params.channel);
    if (params.clientId) next.set("clientId", params.clientId);
    next.set("page", String(targetPage));
    return `/?${next.toString()}`;
  }

  const [notifications, clients] = await Promise.all([
    listNotifications({
      status: params.status,
      channel: params.channel,
      clientId: params.clientId,
      page,
    }),
    listClients(),
  ]);

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="text-xl font-semibold tracking-tight">Notifikace</h1>
        <p className="mt-1 text-sm text-slate-500">
          {notifications.totalElements} celkem
        </p>
      </div>

      <FilterBar clients={clients} />

      <div className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
        <table className="min-w-full divide-y divide-slate-200 text-sm">
          <thead className="bg-slate-50">
            <tr className="text-left text-xs font-medium uppercase tracking-wide text-slate-500">
              <th className="px-4 py-3">Klient</th>
              <th className="px-4 py-3">Kanál</th>
              <th className="px-4 py-3">Příjemce</th>
              <th className="px-4 py-3">Předmět</th>
              <th className="px-4 py-3">Stav</th>
              <th className="px-4 py-3">Vytvořeno</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {notifications.content.map((n) => (
              <tr key={n.id} className="hover:bg-slate-50">
                <td className="px-4 py-3">
                  <Link
                    href={`/notifications/${n.id}`}
                    className="font-medium text-slate-900 hover:underline"
                  >
                    {n.clientName}
                  </Link>
                </td>
                <td className="px-4 py-3">
                  <ChannelBadge channel={n.channel} />
                </td>
                <td className="px-4 py-3 text-slate-600">{n.recipient}</td>
                <td className="px-4 py-3 text-slate-600">{n.subject ?? "—"}</td>
                <td className="px-4 py-3">
                  <StatusBadge status={n.status} />
                </td>
                <td className="px-4 py-3 text-slate-500">{formatDate(n.createdAt)}</td>
              </tr>
            ))}
            {notifications.content.length === 0 && (
              <tr>
                <td colSpan={6} className="px-4 py-10 text-center text-slate-400">
                  Žádné notifikace neodpovídají filtru.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {notifications.totalPages > 1 && (
        <div className="flex items-center justify-between text-sm text-slate-500">
          <span>
            Stránka {notifications.number + 1} z {notifications.totalPages}
          </span>
          <div className="flex gap-2">
            {!notifications.first && (
              <Link
                href={pageHref(page - 1)}
                className="rounded-lg border border-slate-200 bg-white px-3 py-1.5 hover:bg-slate-50"
              >
                Předchozí
              </Link>
            )}
            {!notifications.last && (
              <Link
                href={pageHref(page + 1)}
                className="rounded-lg border border-slate-200 bg-white px-3 py-1.5 hover:bg-slate-50"
              >
                Další
              </Link>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
