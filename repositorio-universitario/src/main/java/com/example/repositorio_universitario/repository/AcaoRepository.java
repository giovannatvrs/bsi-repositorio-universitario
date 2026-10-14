package com.example.repositorio_universitario.repository;

import com.example.repositorio_universitario.domain.Acao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcaoRepository extends JpaRepository<Acao, Integer> {

    Optional<Acao> findByNome(String nome);
}
