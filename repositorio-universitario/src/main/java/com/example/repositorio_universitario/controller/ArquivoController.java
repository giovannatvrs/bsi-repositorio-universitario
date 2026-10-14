package com.example.repositorio_universitario.controller;

import com.example.repositorio_universitario.Enums.StatusArquivo;
import com.example.repositorio_universitario.authentification.CustomOAuth2User;
import com.example.repositorio_universitario.domain.Arquivo;
import com.example.repositorio_universitario.domain.Usuario;
import com.example.repositorio_universitario.repository.ArquivoRepository;
import com.example.repositorio_universitario.service.ArquivoService;
import com.example.repositorio_universitario.service.HistoricoService;
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

import javax.swing.text.html.parser.Entity;
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

    @Autowired
    private HistoricoService historicoService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadArquivo(@RequestParam("file") MultipartFile file, @RequestParam("nome") String nome, @RequestParam("disciplina") String disciplina, @RequestParam("descricao") String descricao, @AuthenticationPrincipal CustomOAuth2User user) throws IOException {
        Usuario usuario = usuarioService.buscarPorEmail(user.getEmail());
        if(usuario.isSuspenso()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Arquivo arquivo = arquivoService.uploadFile(
                file, nome, disciplina, descricao, usuario
        );

        historicoService.registrarAcaoArquivo(
                "UPLOAD", arquivo, usuario
        );

        return ResponseEntity.ok("Upload de arquivo realizado com sucesso!");

    }

    @GetMapping("/visualizar/{id}")
    public ResponseEntity<byte[]> visualizarArquivo(@PathVariable int id) {
        arquivoService.buscarArquivoDisponivel(id);
        Arquivo arquivo = arquivoService.getArquivo(id);

        byte[] data = arquivoService.downloadFile(arquivo.getNome_real_arquivo());

        // Fallback para tipoMime caso esteja nulo no banco
        String tipoMime = arquivo.getTipoMime();
        if (tipoMime == null || tipoMime.isBlank()) {
            tipoMime = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(tipoMime));

        // "inline" indica ao navegador para tentar renderizar (imagem, vídeo, PDF, etc.)
        headers.setContentDisposition(
                ContentDisposition.inline()
                        .filename(arquivo.getNome_real_arquivo())
                        .build()
        );

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadArquivo(@PathVariable("id") int id, @AuthenticationPrincipal CustomOAuth2User user) {
        arquivoService.buscarArquivoDisponivel(id);
        Arquivo arquivo = arquivoService.getArquivo(id);

        byte[] data= arquivoService.downloadFile(arquivo.getNome_real_arquivo());

        String contentType = "application/octet-stream";

        if (user != null) {
            Usuario responsavel = usuarioService.buscarPorEmail(user.getEmail());

            historicoService.registrarAcaoArquivo(
                    "DOWNLOAD", arquivo, responsavel
            );
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + arquivo.getNome()+"\"")
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(data);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<String> deleteArquivo(
            @PathVariable("id") int id,
            @AuthenticationPrincipal CustomOAuth2User user) {

        arquivoService.buscarArquivoDisponivel(id);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuário não autenticado.");
        }

        Usuario responsavel = usuarioService.buscarPorEmail(user.getEmail());

        arquivoService.deletarArquivo(id, responsavel);

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
    public ResponseEntity<Void> aprovarArquivo(@PathVariable ("id") int id, @AuthenticationPrincipal CustomOAuth2User user){
        if (user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario moderador = usuarioService.buscarPorEmail(user.getEmail());

        arquivoService.avaliarArquivo(id, moderador, StatusArquivo.APROVADO);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reprovar/{id}")
    public ResponseEntity<Void> reprovarArquivo(@PathVariable ("id") int id, @AuthenticationPrincipal CustomOAuth2User user){
        if (user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario moderador = usuarioService.buscarPorEmail(user.getEmail());

        arquivoService.avaliarArquivo(id, moderador, StatusArquivo.REPROVADO);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/arquivos/{disciplina}")
    public ResponseEntity<Page<Arquivo>> listarArquivosDisciplina(@PathVariable("disciplina") String disciplina, int page){
        Page<Arquivo> arquivosDisciplina = arquivoService.listarArquivosPorDisciplina(disciplina,StatusArquivo.APROVADO, page);
        return ResponseEntity.ok(arquivosDisciplina);
    }

}
