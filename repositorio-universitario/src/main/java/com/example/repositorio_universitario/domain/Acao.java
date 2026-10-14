package com.example.repositorio_universitario.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name="tb_acao")
@Entity
public class Acao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_acao")
    private Integer id;

    @Column(name="nome_acao")
    private String nome;
}
