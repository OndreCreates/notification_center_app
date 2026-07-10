import Link from "next/link";
import { getNotification } from "@/lib/api";
import { formatDate } from "@/lib/format";
import { StatusBadge } from "@/components/StatusBadge";
import { ChannelBadge } from "@/components/ChannelBadge";

export default async function NotificationDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const notification = await getNotification(id);

  return (
    <div className="flex flex-col gap-6">
      <div>
        <Link href="/" className="text-sm text-slate-500 hover:text-slate-900">
          ← Zpět na seznam
        </Link>
        <div className="mt-2 flex items-center gap-3">
          <h1 className="text-xl font-semibold tracking-tight">
            Notifikace #{notification.id}
          </h1>
          <StatusBadge status={notification.status} />
          <ChannelBadge channel={notification.channel} />
        </div>
      </div>

      <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
        <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
          <h2 className="text-sm font-medium text-slate-500">Detaily</h2>
          <dl className="mt-3 space-y-2 text-sm">
            <div className="flex justify-between gap-4">
              <dt className="text-slate-500">Klient</dt>
              <dd className="font-medium text-slate-900">{notification.clientName}</dd>
            </div>
            <div className="flex justify-between gap-4">
              <dt className="text-slate-500">Příjemce</dt>
              <dd className="font-medium text-slate-900">{notification.recipient}</dd>
            </div>
            <div className="flex justify-between gap-4">
              <dt className="text-slate-500">Předmět</dt>
              <dd className="font-medium text-slate-900">{notification.subject ?? "—"}</dd>
            </div>
            <div className="flex justify-between gap-4">
              <dt className="text-slate-500">Vytvořeno</dt>
              <dd className="text-slate-900">{formatDate(notification.createdAt)}</dd>
            </div>
            <div className="flex justify-between gap-4">
              <dt className="text-slate-500">Aktualizováno</dt>
              <dd className="text-slate-900">{formatDate(notification.updatedAt)}</dd>
            </div>
          </dl>
        </div>

        <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
          <h2 className="text-sm font-medium text-slate-500">Obsah</h2>
          {/* Ne dangerouslySetInnerHTML – body posílá libovolný autentizovaný
              API klient, renderování jako HTML by byl uložený XSS. */}
          <pre className="mt-3 overflow-x-auto whitespace-pre-wrap text-sm text-slate-700">
            {notification.body}
          </pre>
        </div>
      </div>

      <div className="rounded-xl border border-slate-200 bg-white shadow-sm">
        <div className="border-b border-slate-200 px-5 py-3">
          <h2 className="text-sm font-medium text-slate-500">
            Historie pokusů o doručení ({notification.attempts.length})
          </h2>
        </div>
        <table className="min-w-full divide-y divide-slate-200 text-sm">
          <thead className="bg-slate-50">
            <tr className="text-left text-xs font-medium uppercase tracking-wide text-slate-500">
              <th className="px-4 py-3">#</th>
              <th className="px-4 py-3">Stav</th>
              <th className="px-4 py-3">Chyba</th>
              <th className="px-4 py-3">Čas</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {notification.attempts.map((attempt) => (
              <tr key={attempt.attemptNumber}>
                <td className="px-4 py-3 text-slate-500">{attempt.attemptNumber}</td>
                <td className="px-4 py-3">
                  <span
                    className={
                      attempt.status === "SUCCESS"
                        ? "font-medium text-emerald-700"
                        : "font-medium text-red-700"
                    }
                  >
                    {attempt.status}
                  </span>
                </td>
                <td className="max-w-md truncate px-4 py-3 text-slate-500" title={attempt.errorMessage ?? undefined}>
                  {attempt.errorMessage ?? "—"}
                </td>
                <td className="px-4 py-3 text-slate-500">{formatDate(attempt.attemptedAt)}</td>
              </tr>
            ))}
            {notification.attempts.length === 0 && (
              <tr>
                <td colSpan={4} className="px-4 py-10 text-center text-slate-400">
                  Zatím žádný pokus o doručení.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
