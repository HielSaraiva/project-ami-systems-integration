package org.eletra.energy.networkgrpc.services;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eletra.energy.networkgrpc.grpc.EletraISServiceGrpc;
import org.eletra.energy.networkgrpc.grpc.TransactionRequest;
import org.eletra.energy.networkgrpc.grpc.TransactionResponse;
import org.eletra.energy.networkgrpc.models.entities.Ticket;
import org.eletra.energy.networkgrpc.models.enums.TicketStatus;
import org.eletra.energy.networkgrpc.repositories.TicketRepository;
import org.springframework.grpc.server.service.GrpcService;

import java.util.Optional;
import java.util.UUID;

@Log4j2
@GrpcService
@RequiredArgsConstructor
public class TransactionGrpcService extends EletraISServiceGrpc.EletraISServiceImplBase {

    private final TicketService ticketService;
    private final TicketRepository ticketRepository;

    @Override
    public void createTransaction(TransactionRequest request, StreamObserver<TransactionResponse> responseObserver) {

        String json = request.getJsonPayload();

        String ticketId = ticketService.createTicket(json);

        TransactionResponse response =
                TransactionResponse.newBuilder()
                        .setTicketId(ticketId)
                        .setStatus("RECEIVED")
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

        ticketService.sendTicketId(ticketId);

        Optional<Ticket> ticket = ticketRepository.findById(UUID.fromString(ticketId));
        if (ticket.isPresent()) {
            ticket.get().setStatus(TicketStatus.IN_PROGRESS);
            ticketRepository.save(ticket.get());
        }
    }
}
