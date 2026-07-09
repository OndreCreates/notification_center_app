# Notification Center — Admin

Next.js admin panel pro [Notification Center](../README.md). Přehled notifikací
s filtrací, detail s historií pokusů o doručení. Backend (Spring Boot) musí
běžet na `localhost:8080`.

## Spuštění

```bash
npm install
npm run dev
```

Otevři [http://localhost:3000](http://localhost:3000).

Konfigurace v `.env.local` (mimo git):

```
API_BASE_URL=http://localhost:8080
ADMIN_API_KEY=dev-admin-key-change-me
```
