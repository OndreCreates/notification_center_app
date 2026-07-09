import type { NotificationChannel } from "@/lib/types";

const LABELS: Record<NotificationChannel, string> = {
  EMAIL: "E-mail",
  WEBSOCKET: "In-app",
};

export function ChannelBadge({ channel }: { channel: NotificationChannel }) {
  return (
    <span className="inline-flex items-center rounded-full bg-slate-100 px-2.5 py-0.5 text-xs font-medium text-slate-700 ring-1 ring-inset ring-slate-500/10">
      {LABELS[channel]}
    </span>
  );
}
