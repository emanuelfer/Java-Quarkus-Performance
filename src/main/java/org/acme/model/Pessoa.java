package org.acme.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.*;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import org.acme.utils.StringListConverter;
import org.hibernate.annotations.Generated;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pessoas")
@Cacheable
@RegisterForReflection
public class Pessoa extends PanacheEntityBase {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "nome")
    String nome;

    @Column(name = "apelido")
    String apelido;

    @Column(name = "nascimento")
    String nascimento;

    @Column(name = "stack" )
    @Convert(converter = StringListConverter.class)
    private List<String> stack = Collections.emptyList();

    @Column(name = "busca_trgm")
    @Generated
    @JsonIgnore
    public String busca;

    public String getNome() {
        return nome;
    }

    public String getApelido() {
        return apelido;
    }

    public UUID getId() {
        return id;
    }

    public String getNascimento() {
        return nascimento;
    }

    public List<String> getStack() {
        if(this.stack == null || this.stack.isEmpty())
            return null;
        return this.stack;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public void setNascimento(String nascimento) {
        this.nascimento = nascimento;
    }

    public void setStack(List<String> stack) {
//        if (stack == null) {
//            stack = Collections.emptyList();
//        }
        this.stack = stack;
    }
}
