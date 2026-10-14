package com.example.repositorio_universitario.controller;

import com.example.repositorio_universitario.authentification.CustomOAuth2User;
import com.example.repositorio_universitario.domain.Usuario;
import com.example.repositorio_universitario.dto.HistoricoDTO;
import com.example.repositorio_universitario.repository.UsuarioRepository;

import com.example.repositorio_universitario.service.HistoricoService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/historico")
public class HistoricoController {

    private final HistoricoService historicoService;
    private final UsuarioRepository usuarioRepository;

    public HistoricoController(
            HistoricoService historicoService,
            UsuarioRepository usuarioRepository) {
        this.historicoService = historicoService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public Page<HistoricoDTO> buscarHistorico(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal CustomOAuth2User usuarioAutenticado) {

        if (usuarioAutenticado == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
        }

        Usuario usuario = usuarioRepository
                .findByEmail(usuarioAutenticado.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        // Define 5 itens por página (ajuste se quiser mais ou menos)
        int tamanhoPagina = 5;
        return historicoService.buscarHistoricoPaginado(usuario, page, tamanhoPagina);
    }
}
