import type { ClientSummary, NotificationDetail, NotificationSummary, Page } from "./types";

const API_BASE_URL = process.env.API_BASE_URL ?? "http://localhost:8080";
const ADMIN_API_KEY = process.env.ADMIN_API_KEY ?? "";

async function adminFetch<T>(path: string): Promise<T> {
  const res = await fetch(`${API_BASE_URL}${path}`, {
    headers: { "X-Admin-Key": ADMIN_API_KEY },
    cache: "no-store",
  });

  if (!res.ok) {
    throw new Error(`Admin API ${path} selhalo: HTTP ${res.status}`);
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
