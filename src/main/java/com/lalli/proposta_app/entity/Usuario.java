package com.lalli.proposta_app.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private String name;

    private String sobrenome;

    private String cpf;

    private String telefone;

    private Double renda;

    // Como eu mapeei na proposta
    @OneToOne(mappedBy = "usuario")
    @JsonBackReference
    private Proposta proposta;
}
