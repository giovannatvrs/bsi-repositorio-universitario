package com.example.repositorio_universitario.domain;

import com.example.repositorio_universitario.Enums.StatusArquivo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name="tb_arquivo")
@Entity
public class Arquivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_arquivo")
    private int id;

    @Column(name="nome_real_arquivo")
    private String nome_real_arquivo;

    @Column(name="nome_arquivo")
    private String nome;

    @Column(name="disciplina_arquivo")
    private String disciplina;

    @Column(name="data_arquivo")
    private LocalDateTime data;

    @Column(name="url_arquivo")
    private String url;

    @Column(name="descricao_arquivo")
    private String descricao;

    @Column(name="tipo_mime_arquivo")
    private String tipoMime;

    @Enumerated(EnumType.STRING)
    @Column(name="status_arquivo")
    private StatusArquivo status;

    @ManyToOne
    @JoinColumn(name="id_usuario_fk")
    private Usuario usuario;

}
