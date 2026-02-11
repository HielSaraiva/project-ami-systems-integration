package org.eletra.energy.networkgrpc.services;

import io.grpc.stub.StreamObserver;
import org.eletra.energy.networkgrpc.configs.TestcontainersConfig;
import org.eletra.energy.networkgrpc.grpc.TransactionRequest;
import org.eletra.energy.networkgrpc.grpc.TransactionResponse;
import org.eletra.energy.networkgrpc.models.entities.Ticket;
import org.eletra.energy.networkgrpc.repositories.TicketRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;

@Import(TestcontainersConfig.class)
@SpringBootTest
public class TransactionGrpcServiceTest {

    @Autowired
    private TransactionGrpcService transactionGrpcService;

    @MockitoSpyBean
    private TicketService ticketService;

    @MockitoSpyBean
    private TicketRepository ticketRepository;

    @Test
    public void TransactionShouldBeProcessedTest() {
        // Given
        String payload = """
                {
                    "user":
                        {
                            "id":"b16404b4-f690-44dc-8db0-8f48ec568590",
                            "username":"francisco.parreira",
                            "firstName":"Lorraine",
                            "lastName":"Almeida",
                            "employeeCode":"640708",
                            "position":"gardener",
                            "cpf":"534.670.770-05"
                        },
                    "log":
                        {
                            "id":"9580ab40-b0b6-42cb-bb8f-7c1e1f654f6a",
                            "sentAt":"01-27-2026T12:05:04.001Z",
                            "message":"No. Interestingly enough, her leaf blower picked up.",
                            "format":null
                        }
                }""";

        // When
        Assertions.assertDoesNotThrow(() -> {
            transactionGrpcService.createTransaction(
                    TransactionRequest.newBuilder().setJsonPayload(payload).build(),
                    new StreamObserver<>() {
                        @Override
                        public void onNext(TransactionResponse transactionResponse) {
                            // Then
                            assert transactionResponse.getStatus().equals("RECEIVED");
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            assert false : "gRPC call failed with error: " + throwable.getMessage();
                        }

                        @Override
                        public void onCompleted() {
                            // No action needed on completion for this test
                        }
                    }
            );
        });
    }

    @Test
    public void TicketShouldBeCreatedUpdatedAndSentTest() {
        // Given
        String payload = """
                {
                    "user":
                        {
                            "id":"b16404b4-f690-44dc-8db0-8f48ec568590",
                            "username":"francisco.parreira",
                            "firstName":"Lorraine",
                            "lastName":"Almeida",
                            "employeeCode":"640708",
                            "position":"gardener",
                            "cpf":"534.670.770-05"
                        },
                    "log":
                        {
                            "id":"9580ab40-b0b6-42cb-bb8f-7c1e1f654f6a",
                            "sentAt":"01-27-2026T12:05:04.001Z",
                            "message":"No. Interestingly enough, her leaf blower picked up.",
                            "format":null
                        }
                }""";

        // When
        Assertions.assertDoesNotThrow(() -> {
            transactionGrpcService.createTransaction(
                    TransactionRequest.newBuilder().setJsonPayload(payload).build(),
                    new StreamObserver<>() {
                        @Override
                        public void onNext(TransactionResponse transactionResponse) {
                        }

                        @Override
                        public void onError(Throwable throwable) {
                        }

                        @Override
                        public void onCompleted() {
                        }
                    }
            );
        });

        // Then
        Mockito.verify(ticketService, Mockito.times(1)).sendTicketId(anyString());
        Mockito.verify(ticketService, Mockito.times(1)).createTicket(anyString());
        Mockito.verify(ticketRepository, Mockito.times(2)).save(Mockito.any());
    }

    @Test
    public void TicketShouldNotBePresentTest() {
        // Given
        String payload = """
                {
                    "user":
                        {
                            "id":"b16404b4-f690-44dc-8db0-8f48ec568590",
                            "username":"francisco.parreira",
                            "firstName":"Lorraine",
                            "lastName":"Almeida",
                            "employeeCode":"640708",
                            "position":"gardener",
                            "cpf":"534.670.770-05"
                        },
                    "log":
                        {
                            "id":"9580ab40-b0b6-42cb-bb8f-7c1e1f654f6a",
                            "sentAt":"01-27-2026T12:05:04.001Z",
                            "message":"No. Interestingly enough, her leaf blower picked up.",
                            "format":null
                        }
                }""";

            Mockito.doReturn(Optional.empty()).when(ticketRepository).findById(Mockito.any(UUID.class));
//            Mockito.when(ticketRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        // When
        Assertions.assertDoesNotThrow(() -> {
            transactionGrpcService.createTransaction(
                    TransactionRequest.newBuilder().setJsonPayload(payload).build(),
                    new StreamObserver<>() {
                        @Override
                        public void onNext(TransactionResponse transactionResponse) {
                        }

                        @Override
                        public void onError(Throwable throwable) {
                        }

                        @Override
                        public void onCompleted() {
                        }
                    }
            );
        });

        // Then
        Mockito.verify(ticketRepository, Mockito.times(1)).findById(Mockito.any());
    }
}
