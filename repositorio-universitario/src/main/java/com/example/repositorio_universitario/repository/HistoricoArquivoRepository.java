package com.example.repositorio_universitario.repository;

import com.example.repositorio_universitario.domain.HistoricoArquivo;
import com.example.repositorio_universitario.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoArquivoRepository
        extends JpaRepository<HistoricoArquivo, Integer> {

    List<HistoricoArquivo> findByResponsavel(Usuario responsavel);

    List<HistoricoArquivo> findByArquivo_Usuario(Usuario usuario);

    List<HistoricoArquivo> findByArquivo_Id(Integer id);
}


