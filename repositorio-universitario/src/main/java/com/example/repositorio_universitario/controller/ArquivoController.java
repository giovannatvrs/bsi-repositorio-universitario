package com.example.repositorio_universitario.controller;

import com.example.repositorio_universitario.Enums.StatusArquivo;
import com.example.repositorio_universitario.authentification.CustomOAuth2User;
import com.example.repositorio_universitario.domain.Arquivo;
import com.example.repositorio_universitario.domain.Usuario;
import com.example.repositorio_universitario.repository.ArquivoRepository;
import com.example.repositorio_universitario.service.ArquivoService;
import com.example.repositorio_universitario.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
public class ArquivoController {
    @Autowired
    ArquivoService arquivoService;

    @Autowired
    UsuarioService usuarioService;
    @Autowired
    private ArquivoRepository arquivoRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadArquivo(@RequestParam("file") MultipartFile file, @RequestParam("nome") String nome, @RequestParam("disciplina") String disciplina, @RequestParam("descricao") String descricao, @AuthenticationPrincipal CustomOAuth2User user) throws IOException {
        Usuario usuario = usuarioService.buscarPorEmail(user.getEmail());
        if(usuario.isSuspenso()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        arquivoService.uploadFile(file, nome, disciplina, descricao, usuario);
        return ResponseEntity.ok("Upload de arquivo realizado com sucesso!");
    }

    @GetMapping("/visualizar/{id}")
    public ResponseEntity<byte[]> visualizarArquivo(@PathVariable int id) {
        Arquivo arquivo = arquivoService.getArquivo(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(arquivo.getTipoMime())); // ex: "application/pdf"
        headers.setContentDisposition(ContentDisposition.inline().filename(arquivo.getNome_real_arquivo()).build());
        byte[] data= arquivoService.downloadFile(arquivo.getNome_real_arquivo());

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadArquivo(@PathVariable("id") int id) {
        Arquivo arquivo = arquivoService.getArquivo(id);
        byte[] data= arquivoService.downloadFile(arquivo.getNome_real_arquivo());

        String contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + arquivo.getNome()+"\"")
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(data);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<String> deleteArquivo(@PathVariable("id") int id) {
        arquivoService.deletarArquivo(id);
        return ResponseEntity.ok("Deletado com sucesso!");
    }

    @GetMapping("/envios")
    public ResponseEntity<Page<Arquivo>> listarArquivosUsuario(@AuthenticationPrincipal CustomOAuth2User user, int page) {
        Usuario usuario = usuarioService.buscarPorEmail(user.getEmail());
        int idUsuario = usuario.getId();
        Page<Arquivo> arquivosUsuario = arquivoService.listarArquivosUsuario(idUsuario, page);
        return ResponseEntity.ok(arquivosUsuario);
    }

    @GetMapping("/arquivos")
    public ResponseEntity<Page<Arquivo>> listarArquivos(int page){
        Page<Arquivo> arquivos = arquivoService.listarArquivos(page);
        return ResponseEntity.ok(arquivos);
    }

    @GetMapping("/solicitacoes")
    public ResponseEntity<Page<Arquivo>> listarArquivosPendentes(int page) {
        Page<Arquivo> arquivosPendentes = arquivoService.listarArquivosPendentes(page);
        return ResponseEntity.ok(arquivosPendentes);
    }

    @PutMapping("/aprovar/{id}")
    public void aprovarArquivo(@PathVariable ("id") int id){
        Arquivo arquivo = arquivoService.getArquivo(id);
        arquivo.setStatus(StatusArquivo.APROVADO);
        arquivoRepository.save(arquivo);
    }

    @PutMapping("/reprovar/{id}")
    public void reprovarArquivo(@PathVariable ("id") int id){
        Arquivo arquivo = arquivoService.getArquivo(id);
        arquivo.setStatus(StatusArquivo.REPROVADO);
        arquivoRepository.save(arquivo);
    }

}
