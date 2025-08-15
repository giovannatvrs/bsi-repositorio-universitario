package com.example.repositorio_universitario.repository;

import com.example.repositorio_universitario.Enums.StatusArquivo;
import com.example.repositorio_universitario.domain.Arquivo;
import com.example.repositorio_universitario.domain.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArquivoRepository extends JpaRepository<Arquivo, Integer> {
    Page<Arquivo> findByStatus(StatusArquivo statusArquivo, Pageable pageable);
    Page<Arquivo> findByUsuario(Usuario usuario, Pageable pageable);
}
