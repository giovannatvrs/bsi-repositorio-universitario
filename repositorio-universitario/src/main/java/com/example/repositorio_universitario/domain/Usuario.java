package com.example.repositorio_universitario.domain;

import com.example.repositorio_universitario.Enums.FuncaoUsuario;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name="tb_usuario")
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_usuario")
    private int id;

    @Column(name="nome_usuario", nullable=false)
    private String nome;

    @Column(name="email_usuario", nullable=false)
    private String email;

    @Column(name="url_foto", nullable=false)
    private String url_foto;

    @Column(name="suspenso", nullable=false)
    private boolean suspenso;

    @Enumerated(EnumType.STRING)
    @Column(name="funcao_usuario", nullable=false)
    private FuncaoUsuario funcao;

}
