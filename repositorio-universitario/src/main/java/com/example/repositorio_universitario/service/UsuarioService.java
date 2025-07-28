package com.example.repositorio_universitario.service;

import com.example.repositorio_universitario.Enums.FuncaoUsuario;
import com.example.repositorio_universitario.domain.Usuario;
import com.example.repositorio_universitario.repository.UsuarioRepository;
import org.hibernate.id.uuid.UuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UsuarioService {
    @Autowired
    private  UsuarioRepository usuarioRepository;

    public Usuario processarUsuario(OAuth2User oAuth2User) {
        String name = oAuth2User.getAttribute("name");
        String email = oAuth2User.getAttribute("email");
        String picture = oAuth2User.getAttribute("picture");

        Usuario usuario = usuarioRepository.findByEmail(email).orElseGet(() -> {
            Usuario novo = new Usuario();
            novo.setNome(name);
            novo.setEmail(email);
            novo.setUrl_foto(picture);
            if(Objects.equals("giovannatavares@edu.unirio.br", email)){
                novo.setFuncao(FuncaoUsuario.ADMINISTRADOR);
            }
            else{
                novo.setFuncao(FuncaoUsuario.USUARIO);
            }
            novo.setSuspenso(false);
            System.out.println("Função do usuário: " + novo.getFuncao());
            return usuarioRepository.save(novo);
        });

        return usuario;

    }

}
