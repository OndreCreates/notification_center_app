import { listTemplates } from "@/lib/api";
import { CreateTemplateForm } from "@/components/CreateTemplateForm";
import { TemplateList } from "@/components/TemplateList";

export default async function TemplatesPage() {
  const templates = await listTemplates();

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="text-xl font-semibold tracking-tight">Šablony</h1>
        <p className="mt-1 text-sm text-slate-500">{templates.length} celkem</p>
      </div>

      <CreateTemplateForm />
      <TemplateList templates={templates} />
    </div>
  );
}
