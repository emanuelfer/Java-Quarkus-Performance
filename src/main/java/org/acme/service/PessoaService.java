package org.acme.service;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.acme.model.Pessoa;
import org.acme.model.PessoaRepository;
import org.hibernate.reactive.mutiny.Mutiny;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PessoaService {

    @Inject
    PessoaRepository pessoaRepository;

    @WithTransaction
    public Uni<Response> criarPessoa(Pessoa pessoa){
        String apelido = pessoa.getApelido();
        String nome = pessoa.getNome();
        String nascimento = pessoa.getNascimento();

        if (apelido == null || apelido.isBlank() || apelido.length() > 32
                || nome == null || nome.isBlank() || nome.length() > 100
                || nascimento == null || nascimento.isBlank() || invalidStack(pessoa.getStack())
        ) {
            return Uni.createFrom().item(Response.status(422).build());
        }

        if(invalidString(apelido) || invalidString(nome) || invalidDate(nascimento)){
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST).build());
        }

        UUID uuid = UUID.randomUUID();
        pessoa.setId(uuid);
        pessoa.setApelido(pessoa.getApelido());
        pessoa.setNome(pessoa.getNome());
        pessoa.setNascimento(pessoa.getNascimento());
        pessoa.setStack(pessoa.getStack());

        return Pessoa.<Pessoa>find("apelido", apelido)
                .firstResult()
                .onItem()
                .ifNotNull()
                .transformToUni(entity -> Uni.createFrom().item(Response.status(422).build()))
                .onItem()
                .ifNull()
                .switchTo(this.inserirPessoa(pessoa, uuid));
    }

    @WithTransaction
    public Uni<Response> inserirPessoa(Pessoa pessoa, UUID uuid){
//        return Panache
//                .withSession(pessoa::persist)
//                .replaceWith(Response.status(Response.Status.CREATED).entity(pessoa)
//                        .header("Location", "/pessoas/" + uuid.toString())
//                        .build());

        return pessoaRepository
                .persist(pessoa)
                .replaceWith(Response.status(Response.Status.CREATED).entity(pessoa)
                        .header("Location", "/pessoas/" + uuid.toString())
                        .build());
    }

    public boolean invalidDate(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            // Parse the date using the formatter
            LocalDate.parse(date, formatter);
            return false; // Valid date
        } catch (DateTimeParseException e) {
            return true; // Invalid date
        }
    }

    private boolean invalidString(String string){
        return !string.matches("^[a-zA-Z ]+$");
    }

    private boolean invalidStack(List<String> stack) {
        if (stack == null) {
            return false;
        }

        for (String s : stack) {
            if (s.length() > 32) {
                return true;
            }
        }

        return false;
    }
}
