"use client";

import { useEffect, useState } from "react";
import { Client } from "@stomp/stompjs";

interface FeedItem {
  notificationId: number;
  subject: string;
  body: string;
  receivedAt: string;
}

export function LiveFeed() {
  const [connected, setConnected] = useState(false);
  const [items, setItems] = useState<FeedItem[]>([]);

  useEffect(() => {
    const clientId = process.env.NEXT_PUBLIC_DEMO_CLIENT_ID;
    const wsUrl = process.env.NEXT_PUBLIC_WS_URL ?? "ws://localhost:8080/ws";

    const client = new Client({
      brokerURL: wsUrl,
      reconnectDelay: 3000,
      onConnect: () => {
        setConnected(true);
        client.subscribe(`/topic/notifications/${clientId}`, (message) => {
          const payload = JSON.parse(message.body);
          setItems((prev) => [
            { ...payload, receivedAt: new Date().toLocaleTimeString("cs-CZ") },
            ...prev,
          ]);
        });
      },
      onWebSocketClose: () => setConnected(false),
    });

    client.activate();

    return () => {
      client.deactivate();
    };
  }, []);

  return (
    <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
      <div className="flex items-center justify-between">
        <h2 className="text-sm font-medium text-slate-500">Live feed (in-app)</h2>
        <span
          className={`inline-flex items-center gap-1.5 text-xs ${connected ? "text-emerald-600" : "text-slate-400"}`}
        >
          <span className={`h-1.5 w-1.5 rounded-full ${connected ? "bg-emerald-500" : "bg-slate-300"}`} />
          {connected ? "Připojeno" : "Odpojeno"}
        </span>
      </div>

      <div className="mt-3 flex flex-col gap-2">
        {items.length === 0 && (
          <p className="text-sm text-slate-400">
            Zatím žádné zprávy. Pošli notifikaci s kanálem &quot;In-app&quot;.
          </p>
        )}
        {items.map((item, i) => (
          <div key={i} className="rounded-lg border border-slate-100 bg-slate-50 p-3">
            <div className="flex items-center justify-between">
              <span className="text-sm font-medium text-slate-900">{item.subject}</span>
              <span className="text-xs text-slate-400">{item.receivedAt}</span>
            </div>
            <p className="mt-1 text-sm text-slate-600">{item.body}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
