import type {
  ClientSummary,
  CreateClientResult,
  NotificationChannel,
  NotificationDetail,
  NotificationSummary,
  Page,
  Template,
} from "./types";

const API_BASE_URL = process.env.API_BASE_URL ?? "http://localhost:8080";
const ADMIN_API_KEY = process.env.ADMIN_API_KEY ?? "";

export class AdminApiError extends Error {
  constructor(
    message: string,
    public status: number,
  ) {
    super(message);
  }
}

async function adminFetch<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    headers: {
      "X-Admin-Key": ADMIN_API_KEY,
      ...(init?.body ? { "Content-Type": "application/json" } : {}),
      ...init?.headers,
    },
    cache: "no-store",
  });

  if (!res.ok) {
    const body = await res.json().catch(() => null);
    throw new AdminApiError(body?.error ?? `HTTP ${res.status}`, res.status);
  }

  if (res.status === 204) {
    return undefined as T;
  }

  return res.json() as Promise<T>;
}

export interface NotificationListFilters {
  status?: string;
  channel?: string;
  clientId?: string;
  page?: number;
}

export function listNotifications(filters: NotificationListFilters) {
  const params = new URLSearchParams();
  if (filters.status) params.set("status", filters.status);
  if (filters.channel) params.set("channel", filters.channel);
  if (filters.clientId) params.set("clientId", filters.clientId);
  params.set("page", String(filters.page ?? 0));
  params.set("size", "20");

  return adminFetch<Page<NotificationSummary>>(`/api/v1/admin/notifications?${params.toString()}`);
}

export function getNotification(id: string) {
  return adminFetch<NotificationDetail>(`/api/v1/admin/notifications/${id}`);
}

export function listClients() {
  return adminFetch<ClientSummary[]>("/api/v1/admin/clients");
}

export function createClient(name: string, contactEmail: string) {
  return adminFetch<CreateClientResult>("/api/v1/admin/clients", {
    method: "POST",
    body: JSON.stringify({ name, contactEmail: contactEmail || null }),
  });
}

export function setClientActive(id: number, active: boolean) {
  return adminFetch<ClientSummary>(`/api/v1/admin/clients/${id}`, {
    method: "PATCH",
    body: JSON.stringify({ active }),
  });
}

export function listTemplates() {
  return adminFetch<Template[]>("/api/v1/admin/templates");
}

export function createTemplate(code: string, channel: NotificationChannel, content: string) {
  return adminFetch<Template>("/api/v1/admin/templates", {
    method: "POST",
    body: JSON.stringify({ code, channel, content }),
  });
}

export function updateTemplate(id: number, content: string) {
  return adminFetch<Template>(`/api/v1/admin/templates/${id}`, {
    method: "PUT",
    body: JSON.stringify({ content }),
  });
}

export function deleteTemplate(id: number) {
  return adminFetch<void>(`/api/v1/admin/templates/${id}`, { method: "DELETE" });
}
