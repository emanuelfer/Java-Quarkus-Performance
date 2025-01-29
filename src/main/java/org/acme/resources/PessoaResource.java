package org.acme.resources;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.model.Pessoa;
import org.acme.service.PessoaService;

import java.util.UUID;


@Path("/")
@ApplicationScoped
public class PessoaResource {

    @Inject
    PessoaService pessoaService;

    @GET
    @Path("/pessoas/{id}")
    public Uni<Response> get(UUID id){
        return Pessoa.findById(id)
                .onItem().ifNotNull().transform(pessoa -> Response.ok(pessoa).build()) // Return the Pessoa if found
                .onItem().ifNull().continueWith(() -> Response.status(Response.Status.NOT_FOUND).build()) // Return 404 if not found
                .onFailure().recoverWithItem(err -> {
                    // Log the error and return 500
//                    Log.error("Failed to fetch Pessoa with ID: " + id, err);
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("An error occurred while fetching the record.").build();
                });

    }

    @POST
    @Path("/pessoas")
    public Uni<Response> create(Pessoa pessoa){
        return pessoaService.criarPessoa(pessoa)
                .onFailure().recoverWithItem(t -> {
//                    Log.error("Erro ao criar pessoa", t);
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                });
    }

    @GET
    @Path("/pessoas")
    public Uni<Response> search(@QueryParam("t") String term){
        if(term == null || term.isBlank())
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST).build());

        return Pessoa.find("busca like ?1", "%" + term.toLowerCase() + "%")
                .list()
                .onItem().transform(pessoas -> {
//                    if (pessoas == null || pessoas.isEmpty()) {
//                        return Response.status(Response.Status.NOT_FOUND).build();
//                    }
                    return Response.ok(pessoas).build();
                })
                .onFailure().recoverWithItem(err -> {
                    // Log the error
//                    Log.error("Failed to search pessoas with busca: " + term, err);
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                });
    }

    @GET
    @Path("/contagem-pessoas")
    public Uni<Response> count(){
        return Pessoa.count()
                .onItem()
                .transform(t -> Response.status(Response.Status.OK).entity(t).build())
                .onFailure().recoverWithItem(err -> {
                    // Log the error and return 500
                    Log.error("Failed to count pessoas");
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("An error occurred while fetching the record.").build();
                });

    }

    @GET
    @Path("/versao")
    public Uni<Response> versao(){
        return Uni.createFrom().item(
            Response.status(Response.Status.OK).entity("Versao 14")
                    .build()
        );

    }


}
