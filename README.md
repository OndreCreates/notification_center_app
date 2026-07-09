# Notification Center

Multi-channel notification microservice (email, in-app WebSocket) postavený na
Spring Boot + RabbitMQ + MySQL. Portfolio projekt — plná architektura a
rozhodnutí budou popsané zde po dokončení Fáze 4.

> Aktuální stav: Fáze 1, 2 a 3 hotové. Fáze 4 (produkční polish) rozpracovaná — krok 4A hotový.

## Spuštění (lokální vývoj)

```bash
docker compose up -d
./mvnw spring-boot:run
```

Po startu:
- API: http://localhost:8080
- Health check: http://localhost:8080/actuator/health
- RabbitMQ management: http://localhost:15672 (guest/guest)
- Mailhog UI (odchozí e-maily): http://localhost:8025

**Pozn.:** MySQL běží lokálně na portu `3307` (ne výchozím `3306`), protože
3306 je na tomto stroji obsazený jiným projektem.

## Admin panel

Next.js admin dashboard v [`admin/`](admin/README.md) — přehled notifikací
s filtrací a historií pokusů o doručení, správa klientů a šablon. Vyžaduje
běžící backend.

```bash
cd admin
npm install
npm run dev
```

## Demo klient

Next.js ukázková appka v [`demo/`](demo/README.md) — formulář na odeslání
notifikace + live WebSocket feed. Vyžaduje běžící backend a existujícího
klienta (vytvoř přes admin panel).

```bash
cd demo
npm install
npm run dev
```

## Produkční e-mail (SendGrid)

Lokálně se e-maily posílají na Mailhog (nic reálně neodejde). Pro reálné
odeslání přes SendGrid:

1. Založ SendGrid účet, ověř sender identitu pro `ondrecreates@gmail.com`
   (Single Sender Verification), vygeneruj API klíč.
2. Spusť s profilem `demo`:

```bash
SPRING_PROFILES_ACTIVE=demo SENDGRID_API_KEY=<tvůj-klíč> ./mvnw spring-boot:run
```

Nic dalšího se měnit nemusí — `application-demo.yml` přepne jen SMTP
konfiguraci a sender adresu, zbytek (DB, RabbitMQ, admin klíč) zůstává stejný.
