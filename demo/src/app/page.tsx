import { NotificationForm } from "@/components/NotificationForm";
import { LiveFeed } from "@/components/LiveFeed";

export default function Home() {
  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="text-xl font-semibold tracking-tight">Demo klient</h1>
        <p className="mt-1 text-sm text-slate-500">
          Formulář vlevo pošle notifikaci přes Notification Center API. Kanál
          &quot;In-app&quot; se objeví v live feedu vpravo bez refreshe.
        </p>
      </div>

      <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
        <NotificationForm />
        <LiveFeed />
      </div>
    </div>
  );
}
