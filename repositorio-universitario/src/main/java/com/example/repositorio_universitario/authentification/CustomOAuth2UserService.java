package com.example.repositorio_universitario.authentification;

import com.example.repositorio_universitario.Enums.FuncaoUsuario;
import com.example.repositorio_universitario.domain.Usuario;
import com.example.repositorio_universitario.repository.UsuarioRepository;
import com.example.repositorio_universitario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    @Autowired
    private UsuarioService usuarioService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        Usuario usuario = usuarioService.processarUsuario(oAuth2User);

        FuncaoUsuario funcao = usuario.getFuncao();

        return new CustomOAuth2User(oAuth2User, funcao);
    }
}
