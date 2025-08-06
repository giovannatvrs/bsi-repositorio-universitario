package com.example.repositorio_universitario.controller;

import com.example.repositorio_universitario.repository.ArquivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ArquivoController {
    @Autowired
    ArquivoRepository arquivoRepository;


}
