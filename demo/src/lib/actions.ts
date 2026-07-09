"use server";

const API_BASE_URL = process.env.API_BASE_URL ?? "http://localhost:8080";
const DEMO_CLIENT_API_KEY = process.env.DEMO_CLIENT_API_KEY ?? "";

export interface SendNotificationInput {
  channel: "EMAIL" | "WEBSOCKET";
  recipient: string;
  subject: string;
  body: string;
}

export interface SendNotificationResult {
  id: number;
  status: string;
}

export async function sendNotificationAction(input: SendNotificationInput): Promise<SendNotificationResult> {
  const res = await fetch(`${API_BASE_URL}/api/v1/notifications`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "X-API-Key": DEMO_CLIENT_API_KEY,
    },
    body: JSON.stringify(input),
  });

  if (!res.ok) {
    const body = await res.json().catch(() => null);
    throw new Error(body?.error ?? `Odeslání selhalo: HTTP ${res.status}`);
  }

  return res.json() as Promise<SendNotificationResult>;
}
