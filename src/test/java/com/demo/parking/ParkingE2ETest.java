package com.demo.parking;

import com.demo.parking.domain.Payment;
import com.demo.parking.domain.Ticket;
import com.demo.parking.domain.Vehicle;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ParkingE2ETest {

    @LocalServerPort
    private int serverPort;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.registerModule(new JavaTimeModule());
    }

    private String baseUrl;

    @BeforeAll
    void setup() {
        baseUrl = "http://localhost:" + serverPort + "/parking/";
    }

    @Test
    @SneakyThrows
    void parkingTest() {
        TestRestTemplate restTemplate = new TestRestTemplate();
        var response = restTemplate.postForEntity(baseUrl + "park", "CAR 1", Ticket.class);
        Assertions.assertThat(response).isNotNull()
                .satisfies((Consumer<ResponseEntity<Ticket>>) ticketResponseEntity -> {
                    Assertions.assertThat(ticketResponseEntity.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
                    Assertions.assertThat(ticketResponseEntity.hasBody()).isTrue();
                });
        Assertions.assertThat(response.getBody()).satisfies(ticket -> {
            Assertions.assertThat(ticket.id()).isEqualTo(1);
            Assertions.assertThat(ticket.issuedOn()).isBeforeOrEqualTo(LocalDateTime.now());
        });
        var paymentResponse = restTemplate.postForEntity(baseUrl + "exit", response, Payment.class);
        Assertions.assertThat(paymentResponse).isNotNull()
                .satisfies((Consumer<ResponseEntity<Payment>>) ticketResponseEntity -> {
                    Assertions.assertThat(ticketResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
                    Assertions.assertThat(ticketResponseEntity.hasBody()).isTrue();
                });
        Assertions.assertThat(paymentResponse.getBody()).satisfies(payment ->
                Assertions.assertThat(payment.price()).isEqualTo(2D));
    }

    @Test
    @SneakyThrows
    void concurrentParkingTest() {
        TestRestTemplate restTemplate = new TestRestTemplate();
        ForkJoinPool pool = new ForkJoinPool(20);
        var ticketFutures = new ArrayList<CompletableFuture<ResponseEntity<Ticket>>>();
        IntStream.range(0, 20).boxed().forEach(range -> {
            var ticketFuture = CompletableFuture.supplyAsync(() -> "Car-" + range, pool)
                    .thenApplyAsync(registrationNum ->
                            restTemplate.postForEntity(baseUrl + "park", registrationNum, Ticket.class));
            ticketFutures.add(ticketFuture);
        });
        Assertions.assertThatNoException().isThrownBy(() ->
                CompletableFuture.allOf(ticketFutures.toArray(CompletableFuture[]::new)).join());
        var tickets = ticketFutures.stream().map(CompletableFuture::join).toList();
        Assertions.assertThat(tickets).allSatisfy((Consumer<ResponseEntity<Ticket>>) ticketResponseEntity -> {
            Assertions.assertThat(ticketResponseEntity.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
            Assertions.assertThat(ticketResponseEntity.hasBody()).isTrue();
        });
        var paymentFutures = new ArrayList<CompletableFuture<ResponseEntity<Payment>>>();
        tickets.forEach(ticket -> {
            var paymentFuture = CompletableFuture.supplyAsync(() -> ticket, pool)
                    .thenApplyAsync(ticketToExit ->
                            restTemplate.postForEntity(baseUrl + "exit", ticketToExit.getBody(), Payment.class));
            paymentFutures.add(paymentFuture);
        });
        Assertions.assertThatNoException().isThrownBy(() ->
                CompletableFuture.allOf(paymentFutures.toArray(CompletableFuture[]::new)).join());
        var payments = paymentFutures.stream().map(CompletableFuture::join).toList();
        Assertions.assertThat(payments).allSatisfy((Consumer<ResponseEntity<Payment>>) paymentResponseEntity -> {
            Assertions.assertThat(paymentResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            Assertions.assertThat(paymentResponseEntity.getBody()).isNotNull();
            Assertions.assertThat(paymentResponseEntity.getBody().price()).isEqualTo(2D);
        });
        var vehiclesCollectionEntity = restTemplate.exchange(new RequestEntity<>(HttpMethod.GET,
                        new URI(baseUrl + "admin/vehicles")),
                new ParameterizedTypeReference<Collection<Vehicle>>() {
                });
        Assertions.assertThat(vehiclesCollectionEntity)
                .satisfies((Consumer<ResponseEntity<Collection<Vehicle>>>) vehiclesResponseEntity -> {
                    Assertions.assertThat(vehiclesResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
                    Assertions.assertThat(vehiclesResponseEntity.getBody()).isNotNull();
                    Assertions.assertThat(vehiclesResponseEntity.getBody()).hasSize(0);
                });
    }
}
