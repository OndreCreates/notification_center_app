# Notification Center

Multi-channel notification microservice (email, in-app WebSocket) postavený na
Spring Boot + RabbitMQ + MySQL. Portfolio projekt — plná architektura a
rozhodnutí budou popsané zde po dokončení Fáze 4.

> Aktuální stav: Fáze 1, krok 1A (infrastruktura + datový model) hotový.

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
