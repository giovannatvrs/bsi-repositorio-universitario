package com.example.repositorio_universitario.controller;

import com.example.repositorio_universitario.Enums.FuncaoUsuario;
import com.example.repositorio_universitario.authentification.CustomOAuth2User;
import com.example.repositorio_universitario.domain.Usuario;
import com.example.repositorio_universitario.repository.UsuarioRepository;
import com.example.repositorio_universitario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.example.repositorio_universitario.service.HistoricoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private HistoricoService historicoService;


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
    public ResponseEntity<Page<Usuario>> usuariosComuns(int page){
        Page<Usuario> usuariosComuns = usuarioService.listarUsuariosComuns(page);
        return ResponseEntity.ok(usuariosComuns);
    }

    @GetMapping("/moderadores")
    public ResponseEntity<Page<Usuario>> moderadores(int page) {
        Page<Usuario> moderadores = usuarioService.listarModeradores(page);
       return ResponseEntity.ok(moderadores);
    }

    @PutMapping("/promover/{id}")
    @Transactional
    public ResponseEntity<Void> promoverUsuarioParaModerador(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal CustomOAuth2User autenticado) {

        Usuario responsavel = obterResponsavel(autenticado);
        exigirFuncao(responsavel, FuncaoUsuario.ADMINISTRADOR);

        Usuario usuario = usuarioService.buscarPorId(id);

        if (usuario.getFuncao() != FuncaoUsuario.MODERADOR) {
            usuario.setFuncao(FuncaoUsuario.MODERADOR);
            usuarioRepository.save(usuario);

            historicoService.registrarAcaoUsuario(
                    "CONCESSAO_PAPEL", usuario, responsavel);
        }

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/retirar-papel/{id}")
    @Transactional
    public ResponseEntity<Void> retirarPapel(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal CustomOAuth2User autenticado) {

        Usuario responsavel = obterResponsavel(autenticado);
        exigirFuncao(responsavel, FuncaoUsuario.ADMINISTRADOR);

        Usuario usuario = usuarioService.buscarPorId(id);

        if (usuario.getFuncao() == FuncaoUsuario.MODERADOR) {
            usuario.setFuncao(FuncaoUsuario.USUARIO);
            usuarioRepository.save(usuario);

            historicoService.registrarAcaoUsuario(
                    "REMOCAO_PAPEL", usuario, responsavel);
        }

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/suspender/{id}")
    @Transactional
    public ResponseEntity<Void> suspenderUsuario(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal CustomOAuth2User autenticado) {

        Usuario responsavel = obterResponsavel(autenticado);
        exigirFuncao(responsavel, FuncaoUsuario.MODERADOR);

        if (responsavel.getId().equals(id)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Você não pode suspender a sua própria conta."
            );
        }

        Usuario usuario = usuarioService.buscarPorId(id);

        if (usuario.getFuncao() == FuncaoUsuario.ADMINISTRADOR) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Não é permitido suspender um Administrador."
            );
        }

        if (responsavel.getFuncao() == FuncaoUsuario.MODERADOR
                && usuario.getFuncao() == FuncaoUsuario.MODERADOR) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Moderadores não podem suspender outros Moderadores."
            );
        }

        if (!usuario.isSuspenso()) {
            usuario.setSuspenso(true);
            usuarioRepository.save(usuario);

            historicoService.registrarAcaoUsuario(
                    "SUSPENSAO", usuario, responsavel);
        }

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/retirar-suspensao/{id}")
    @Transactional
    public ResponseEntity<Void> retirarSuspensao(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal CustomOAuth2User autenticado) {

        Usuario responsavel = obterResponsavel(autenticado);
        exigirFuncao(responsavel, FuncaoUsuario.MODERADOR);

        Usuario usuario = usuarioService.buscarPorId(id);

        if (usuario.isSuspenso()) {
            usuario.setSuspenso(false);
            usuarioRepository.save(usuario);

            historicoService.registrarAcaoUsuario(
                    "RETIRADA_SUSPENSAO", usuario, responsavel);
        }

        return ResponseEntity.noContent().build();
    }

    private Usuario obterResponsavel(CustomOAuth2User autenticado) {
        if (autenticado == null || autenticado.getEmail() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado"
            );
        }

        return usuarioRepository.findByEmail(autenticado.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário não encontrado"
                ));
    }

    private void exigirFuncao(
            Usuario usuario,
            FuncaoUsuario funcaoNecessaria) {

        if (usuario.getFuncao() != funcaoNecessaria) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não tem permissão para realizar esta operação"
            );
        }
    }

}
