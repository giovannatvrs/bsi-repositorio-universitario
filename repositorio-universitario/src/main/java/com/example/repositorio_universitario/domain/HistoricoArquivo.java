package com.example.repositorio_universitario.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name="tb_historico_arquivo")
@Entity
public class HistoricoArquivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_historico_arquivo")
    private Integer id;

    @ManyToOne
    @JoinColumn(name="id_arquivo_fk")
    private Arquivo arquivo;

    @Column(name = "nome_arquivo_historico", nullable = false)
    private String nomeArquivo;

    @ManyToOne
    @JoinColumn(name="id_acao_fk")
    private Acao acao;

    @ManyToOne
    @JoinColumn(name = "id_responsavel_fk", nullable = false)
    private Usuario responsavel;

    @Column(name="data_registro")
    private LocalDateTime data;

}
