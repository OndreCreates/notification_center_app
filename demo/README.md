# Notification Center — Demo klient

Ukázková appka pro [Notification Center](../README.md). Formulář pošle
notifikaci přes API, live WebSocket feed zobrazí in-app notifikace bez
refreshe. Backend musí běžet na `localhost:8080` a mít vytvořeného klienta
(viz admin panel → Klienti).

## Spuštění

```bash
npm install
npm run dev
```

Otevři [http://localhost:3000](http://localhost:3000).

Konfigurace v `.env.local` (mimo git):

```
API_BASE_URL=http://localhost:8080
DEMO_CLIENT_API_KEY=<api-key-demo-klienta-z-admin-panelu>
NEXT_PUBLIC_DEMO_CLIENT_ID=<id-demo-klienta>
NEXT_PUBLIC_WS_URL=ws://localhost:8080/ws
```
