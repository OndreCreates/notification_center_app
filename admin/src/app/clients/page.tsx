import { listClients } from "@/lib/api";
import { CreateClientForm } from "@/components/CreateClientForm";
import { ClientTable } from "@/components/ClientTable";

export default async function ClientsPage() {
  const clients = await listClients();

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="text-xl font-semibold tracking-tight">Klienti</h1>
        <p className="mt-1 text-sm text-slate-500">{clients.length} celkem</p>
      </div>

      <CreateClientForm />
      <ClientTable clients={clients} />
    </div>
  );
}
