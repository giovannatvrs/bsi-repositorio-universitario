package com.example.repositorio_universitario.dto;

import java.time.LocalDateTime;

public record HistoricoDTO(
        LocalDateTime data,
        String acao,
        String item,
        String responsavel
) {}
