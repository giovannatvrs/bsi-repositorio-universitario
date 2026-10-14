package com.example.repositorio_universitario.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name="tb_historico_usuario")
@Entity
public class HistoricoUsuario {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name="id_historico_usuario")
     private Integer id;

    @Column(name="data_registro")
    private LocalDateTime data;

    @ManyToOne
    @JoinColumn(name="id_acao_fk")
    private Acao acao;

    @ManyToOne
    @JoinColumn(name="id_usuario_fk")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name="id_responsavel_fk")
    private Usuario responsavel;

}
