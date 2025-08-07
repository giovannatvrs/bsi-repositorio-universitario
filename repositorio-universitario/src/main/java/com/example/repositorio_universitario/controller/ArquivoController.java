package com.example.repositorio_universitario.controller;

import com.example.repositorio_universitario.authentification.CustomOAuth2User;
import com.example.repositorio_universitario.domain.Usuario;
import com.example.repositorio_universitario.repository.ArquivoRepository;
import com.example.repositorio_universitario.service.ArquivoService;
import com.example.repositorio_universitario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.Principal;

@Controller
public class ArquivoController {
    @Autowired
    ArquivoService arquivoService;

    @Autowired
    UsuarioService usuarioService;

    @PostMapping("/upload")
    public String fazerUploadArquivo(@RequestParam("file") MultipartFile file, @RequestParam("disciplina") String disciplina, @RequestParam("descricao") String descricao, @AuthenticationPrincipal CustomOAuth2User user) throws IOException {
        Usuario usuario = usuarioService.buscarPorEmail(user.getEmail());
        arquivoService.uploadFile(file, disciplina, descricao, usuario);
        return "redirect:/usuario";
    }

}
