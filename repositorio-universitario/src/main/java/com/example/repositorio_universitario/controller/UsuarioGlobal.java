package com.example.repositorio_universitario.controller;

import com.example.repositorio_universitario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UsuarioGlobal {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/usuario-info")
    public ResponseEntity<?> usuarioLogado(@AuthenticationPrincipal OAuth2User oAuth2User) {
        String name = oAuth2User.getAttribute("name");
        String email = oAuth2User.getAttribute("email");
        String picture = oAuth2User.getAttribute("picture");

        return usuarioRepository.findByEmail(email).map(usuario -> {
            Map<String, Object> resposta = new HashMap<>();
            resposta.put("id", usuario.getId());
            resposta.put("name", name);
            resposta.put("email", email);
            resposta.put("picture", picture);
            resposta.put("suspenso", usuario.isSuspenso());
            resposta.put("funcao", usuario.getFuncao());
            return ResponseEntity.ok(resposta);
        }).orElseGet(() -> {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Usuário não encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
        });

    }


}
