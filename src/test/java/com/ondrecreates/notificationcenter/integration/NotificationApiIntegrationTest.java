package com.ondrecreates.notificationcenter.integration;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.ondrecreates.notificationcenter.client.Client;
import com.ondrecreates.notificationcenter.client.ClientRepository;
import com.ondrecreates.notificationcenter.security.ApiKeyHasher;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * End-to-end: POST /api/v1/notifications → RabbitMQ → EmailNotificationConsumer
 * → skutečně odeslaný e-mail. MySQL a RabbitMQ přes Testcontainers (skutečná
 * infrastruktura, ne mocky/embedded náhrady – to je celý smysl integračního
 * testu). SMTP přes GreenMail (in-JVM fake server) místo dalšího Testcontainers
 * kontejneru pro Mailhog – jednodušší a rychlejší pro pouhé zachycení e-mailu.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class NotificationApiIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4")
            .withDatabaseName("notification_center")
            .withUsername("notification_center")
            .withPassword("notification_center");

    @Container
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3.13-management");

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP);

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);

        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitmq::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitmq::getAdminPassword);

        registry.add("spring.mail.host", () -> "localhost");
        registry.add("spring.mail.port", () -> ServerSetupTest.SMTP.getPort());
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClientRepository clientRepository;

    private String apiKey;

    @BeforeEach
    void setUp() {
        apiKey = UUID.randomUUID().toString();
        Client client = Client.builder()
                .name("Integration Test Client")
                .apiKeyHash(ApiKeyHasher.hash(apiKey))
                .active(true)
                .build();
        clientRepository.save(client);
    }

    @Test
    void postingNotification_getsDeliveredByEmailConsumer() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-Key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "channel", "EMAIL",
                "recipient", "integration@example.com",
                "subject", "Integrační test",
                "body", "Ahoj z integračního testu"
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/notifications", new HttpEntity<>(body, headers), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).containsEntry("status", "PENDING");

        MimeMessage[] messages = awaitReceivedMessages();
        assertThat(messages).hasSize(1);
        assertThat(messages[0].getSubject()).isEqualTo("Integrační test");
    }

    @Test
    void postingNotification_withoutApiKey_isRejected() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "channel", "EMAIL",
                "recipient", "nope@example.com",
                "subject", "X",
                "body", "Y"
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/notifications", new HttpEntity<>(body, headers), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private MimeMessage[] awaitReceivedMessages() throws InterruptedException {
        long deadline = System.currentTimeMillis() + 10_000;
        while (System.currentTimeMillis() < deadline) {
            MimeMessage[] messages = greenMail.getReceivedMessages();
            if (messages.length > 0) {
                return messages;
            }
            Thread.sleep(200);
        }
        fail("E-mail nebyl doručen do 10s");
        return new MimeMessage[0];
    }
}
