import type { NotificationStatus } from "@/lib/types";

const STYLES: Record<NotificationStatus, string> = {
  PENDING: "bg-amber-50 text-amber-700 ring-amber-600/20",
  SENT: "bg-emerald-50 text-emerald-700 ring-emerald-600/20",
  DEAD: "bg-red-50 text-red-700 ring-red-600/20",
};

export function StatusBadge({ status }: { status: NotificationStatus }) {
  return (
    <span
      className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ring-1 ring-inset ${STYLES[status]}`}
    >
      {status}
    </span>
  );
}
