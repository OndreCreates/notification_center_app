"use server";

import { revalidatePath } from "next/cache";
import * as api from "./api";
import type { NotificationChannel } from "./types";

export async function createClientAction(name: string, contactEmail: string) {
  const result = await api.createClient(name, contactEmail);
  revalidatePath("/clients");
  return result;
}

export async function setClientActiveAction(id: number, active: boolean) {
  await api.setClientActive(id, active);
  revalidatePath("/clients");
}

export async function createTemplateAction(code: string, channel: NotificationChannel, content: string) {
  await api.createTemplate(code, channel, content);
  revalidatePath("/templates");
}

export async function updateTemplateAction(id: number, content: string) {
  await api.updateTemplate(id, content);
  revalidatePath("/templates");
}

export async function deleteTemplateAction(id: number) {
  await api.deleteTemplate(id);
  revalidatePath("/templates");
}
