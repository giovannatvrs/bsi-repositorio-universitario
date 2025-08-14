package com.example.repositorio_universitario.controller;

import ch.qos.logback.core.model.Model;
import com.example.repositorio_universitario.Enums.FuncaoUsuario;
import com.example.repositorio_universitario.authentification.CustomOAuth2User;
import com.example.repositorio_universitario.domain.Usuario;
import com.example.repositorio_universitario.repository.UsuarioRepository;
import com.example.repositorio_universitario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;


    @GetMapping("/usuario-logado")
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

    @GetMapping("/usuarios")
    public ResponseEntity<Page<Usuario>> usuarios(int page) {
        Page<Usuario> usuarios = usuarioService.listarUsuarios(page);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/usuarios-comuns")
    public ResponseEntity<List<Usuario>> usuariosComuns(){
        List<Usuario> usuariosComuns = usuarioService.listarUsuariosComuns();
        return ResponseEntity.ok(usuariosComuns);
    }

    @GetMapping("/moderadores")
    public ResponseEntity<List<Usuario>> moderadores() {
        List<Usuario> moderadores = usuarioService.listarModeradores();
       return ResponseEntity.ok(moderadores);
    }

    @PutMapping("/promover/{id}")
    public void promoverUsuarioParaModerador(@PathVariable("id") Integer id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        usuario.setFuncao(FuncaoUsuario.MODERADOR);
        usuarioRepository.save(usuario);
    }

    @PutMapping("/retirar-papel/{id}")
    public void retirarPapel(@PathVariable("id") Integer id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        usuario.setFuncao(FuncaoUsuario.USUARIO);
        usuarioRepository.save(usuario);
    }
    @PutMapping("/suspender/{id}")
    public void suspenderUsuario(@PathVariable ("id") Integer id){
        Usuario usuario = usuarioService.buscarPorId(id);
        usuario.setSuspenso(true);
        usuarioRepository.save(usuario);
    }
    @PutMapping("/tirar-suspensao/{id}")
    public void retirarSuspensão(@PathVariable ("id") Integer id){
        Usuario usuario = usuarioService.buscarPorId(id);
        usuario.setSuspenso(false);
        usuarioRepository.save(usuario);
    }
}
