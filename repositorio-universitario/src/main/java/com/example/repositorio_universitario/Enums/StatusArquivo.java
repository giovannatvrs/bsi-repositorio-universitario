package com.example.repositorio_universitario.Enums;

import lombok.Getter;

@Getter
public enum StatusArquivo {
    PENDENTE("Pendente"),
    APROVADO("Aprovado"),
    REPROVADO("Reprovado");

    private String statusArquivo;
    private StatusArquivo(String statusArquivo) {
        this.statusArquivo = statusArquivo;
    }


}
