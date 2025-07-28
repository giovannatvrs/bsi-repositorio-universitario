package com.example.repositorio_universitario.authentification;

import com.example.repositorio_universitario.Enums.FuncaoUsuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;


public class CustomOAuth2User implements OAuth2User {

    private OAuth2User oAuth2User;
    private FuncaoUsuario funcao;

    public CustomOAuth2User(OAuth2User oAuth2User, FuncaoUsuario funcao) {
        super();
        this.oAuth2User = oAuth2User;
        this.funcao = funcao;
    }

    @Override
    public Map<String, Object> getAttributes(){
        return oAuth2User.getAttributes();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName(){
        return oAuth2User.getAttribute("name");
    }


    public String getEmail() {
        return oAuth2User.getAttribute("email");
    }
}
