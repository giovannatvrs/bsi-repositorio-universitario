package com.example.repositorio_universitario.repository;

import com.example.repositorio_universitario.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByFuncao(String funcao);

    @Override
    Optional<Usuario> findById(Integer integer);
}
