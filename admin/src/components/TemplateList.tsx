"use client";

import { useState, useTransition } from "react";
import { deleteTemplateAction, updateTemplateAction } from "@/lib/actions";
import type { Template } from "@/lib/types";
import { ChannelBadge } from "./ChannelBadge";

function TemplateRow({ template }: { template: Template }) {
  const [isPending, startTransition] = useTransition();
  const [editing, setEditing] = useState(false);
  const [content, setContent] = useState(template.content);
  const [error, setError] = useState<string | null>(null);

  function save() {
    setError(null);
    startTransition(async () => {
      try {
        await updateTemplateAction(template.id, content);
        setEditing(false);
      } catch (e) {
        setError(e instanceof Error ? e.message : "Uložení selhalo.");
      }
    });
  }

  function remove() {
    if (!confirm(`Opravdu smazat šablonu "${template.code}"?`)) return;
    startTransition(async () => {
      await deleteTemplateAction(template.id);
    });
  }

  return (
    <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
      <div className="flex items-center justify-between gap-3">
        <div className="flex items-center gap-3">
          <span className="font-mono text-sm font-medium text-slate-900">{template.code}</span>
          <ChannelBadge channel={template.channel} />
        </div>
        <div className="flex gap-2">
          <button
            onClick={() => setEditing((v) => !v)}
            className="rounded-lg border border-slate-200 px-3 py-1.5 text-xs font-medium text-slate-700 hover:bg-slate-50"
          >
            {editing ? "Zrušit" : "Upravit"}
          </button>
          <button
            onClick={remove}
            disabled={isPending}
            className="rounded-lg border border-red-200 px-3 py-1.5 text-xs font-medium text-red-700 hover:bg-red-50 disabled:opacity-50"
          >
            Smazat
          </button>
        </div>
      </div>

      {editing ? (
        <div className="mt-3 flex flex-col gap-2">
          <textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            rows={5}
            className="rounded-lg border border-slate-200 px-3 py-2 font-mono text-sm focus:border-slate-400 focus:outline-none"
          />
          {error && <p className="text-sm text-red-600">{error}</p>}
          <button
            onClick={save}
            disabled={isPending}
            className="self-start rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-700 disabled:opacity-50"
          >
            {isPending ? "Ukládám…" : "Uložit"}
          </button>
        </div>
      ) : (
        <pre className="mt-3 overflow-x-auto rounded-lg bg-slate-50 p-3 font-mono text-xs text-slate-600">
          {template.content}
        </pre>
      )}
    </div>
  );
}

export function TemplateList({ templates }: { templates: Template[] }) {
  if (templates.length === 0) {
    return <p className="text-sm text-slate-400">Zatím žádné šablony.</p>;
  }

  return (
    <div className="flex flex-col gap-4">
      {templates.map((template) => (
        <TemplateRow key={template.id} template={template} />
      ))}
    </div>
  );
}
