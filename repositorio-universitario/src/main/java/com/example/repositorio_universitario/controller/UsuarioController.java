package com.example.repositorio_universitario.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UsuarioController {

    @GetMapping("/api/user")
    public ResponseEntity<Map<String, Object>> getUserInfo(OAuth2AuthenticationToken auth) {
        if(auth == null){
            return ResponseEntity.ok(null);
        }
        String name = auth.getPrincipal().getAttribute("name");
        String email = auth.getPrincipal().getAttribute("email");
        String picture = auth.getPrincipal().getAttribute("picture");
        if(picture == null){
            picture = "default.png";
        }
        Map<String, Object> map = Map.of("name", name, "email", email, "picture", picture);
        return ResponseEntity.ok(map);
    }
}
