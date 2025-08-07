package com.example.repositorio_universitario.controller;

import ch.qos.logback.core.model.Model;
import com.example.repositorio_universitario.Enums.FuncaoUsuario;
import com.example.repositorio_universitario.authentification.CustomOAuth2User;
import com.example.repositorio_universitario.domain.Usuario;
import com.example.repositorio_universitario.repository.UsuarioRepository;
import com.example.repositorio_universitario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;


    @GetMapping("/usuario-logado")
    public ModelAndView usuarioLogado(@AuthenticationPrincipal OAuth2User oAuth2User) {
        ModelAndView modelAndView = new ModelAndView("usuario");

        return modelAndView;

    }

    @GetMapping("/usuarios")
    public ModelAndView usuarios() {
        ModelAndView modelAndView = new ModelAndView("usuarios");
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        modelAndView.addObject("usuarios", usuarios);
        return modelAndView;
    }

    @GetMapping("/moderadores")
    public ModelAndView moderadores() {
        ModelAndView modelAndView = new ModelAndView("moderadores");
        List<Usuario> moderadores = usuarioService.listarModeradores();
        modelAndView.addObject("moderadores", moderadores);
        return modelAndView;
    }

    @PostMapping("/promover/{id}")
    public String promoverUsuarioParaModerador(@PathVariable("id") Integer id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        usuario.setFuncao(FuncaoUsuario.MODERADOR);
        usuarioRepository.save(usuario);
        return "redirect:/usuarios";
    }

    @PostMapping("/retirar-papel/{id}")
    public String retirarPapel(@PathVariable("id") Integer id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        usuario.setFuncao(FuncaoUsuario.USUARIO);
        usuarioRepository.save(usuario);
        return "redirect:/moderadores";
    }


}
