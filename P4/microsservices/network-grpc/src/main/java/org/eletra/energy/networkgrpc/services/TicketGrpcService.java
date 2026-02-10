package org.eletra.energy.networkgrpc.services;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.eletra.energy.networkgrpc.grpc.TicketRequest;
import org.eletra.energy.networkgrpc.grpc.TicketResponse;
import org.eletra.energy.networkgrpc.grpc.TicketServiceGrpc;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class TicketGrpcService extends TicketServiceGrpc.TicketServiceImplBase {

    private final TicketService ticketService;

    @Override
    public void createTicket(TicketRequest request, StreamObserver<TicketResponse> responseObserver) {

        String json = request.getJsonPayload();

        String ticketId = ticketService.createTicket(json);

        TicketResponse response =
                TicketResponse.newBuilder()
                        .setTicketId(ticketId)
                        .setStatus("RECEIVED")
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
