package com.example.repositorio_universitario.service;


import com.example.repositorio_universitario.Enums.StatusArquivo;
import com.example.repositorio_universitario.domain.Arquivo;
import com.example.repositorio_universitario.domain.HistoricoArquivo;
import com.example.repositorio_universitario.domain.Usuario;
import com.example.repositorio_universitario.repository.ArquivoRepository;
import com.example.repositorio_universitario.repository.HistoricoArquivoRepository;
import com.example.repositorio_universitario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;
import java.util.UUID;

@Service
public class ArquivoService {
    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.region}")
    private String region;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private ArquivoRepository arquivoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private HistoricoService historicoService;

    @Autowired
    private HistoricoArquivoRepository historicoArquivoRepository;

    public Arquivo uploadFile(MultipartFile file, String nome, String disciplina, String descricao, Usuario usuario) throws IOException{
        String fileName = UUID.randomUUID() + "-"+ file.getOriginalFilename();

        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName).build(), RequestBody.fromBytes(file.getBytes()));
        String url = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + fileName;
        Arquivo arquivo = new Arquivo();
        arquivo.setNome(nome);
        arquivo.setNome_real_arquivo(fileName);
        arquivo.setDisciplina(disciplina);
        arquivo.setData(LocalDateTime.now());
        arquivo.setUrl(url);
        arquivo.setDescricao(descricao);
        arquivo.setStatus(StatusArquivo.PENDENTE);
        arquivo.setUsuario(usuario);

        String tipoMime = file.getContentType();
        arquivo.setTipoMime(tipoMime != null ? tipoMime : "application/octet-stream");

        return arquivoRepository.save(arquivo);
    }


    public Arquivo buscarArquivoDisponivel(int id) {
        Arquivo arquivo = getArquivo(id);

        if (arquivo.getStatus() == StatusArquivo.REPROVADO) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Este arquivo foi reprovado e não está disponível para acesso."
            );
        }

        return arquivo;
    }


    public byte[] downloadFile(String key) {
        ResponseBytes<GetObjectResponse> objectAsBytes = s3Client.getObjectAsBytes(GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
        return objectAsBytes.asByteArray();
    }

    @Transactional
    public void deletarArquivo(int id, Usuario responsavel){
        Arquivo arquivo = getArquivo(id);
        if(arquivo.getUsuario()== null || !arquivo.getUsuario().getId().equals(responsavel.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário não possui permissão para deletar o arquivo");
        }

        historicoService.registrarAcaoArquivo(
                "EXCLUSAO", arquivo, responsavel
        );

        List<HistoricoArquivo> historicos =
                historicoArquivoRepository.findByArquivo_Id(arquivo.getId());

        for (HistoricoArquivo historico : historicos) {
            historico.setArquivo(null);
        }

        historicoArquivoRepository.saveAll(historicos);
        historicoArquivoRepository.flush();

        // Exclui o registro do banco.
        arquivoRepository.delete(arquivo);
        arquivoRepository.flush();

        String key = arquivo.getUrl().substring(arquivo.getUrl().lastIndexOf("/") + 1);
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
        arquivoRepository.delete(arquivo);

    }


    public void avaliarArquivo(
            int id,
            Usuario moderador,
            StatusArquivo novoStatus) {



        Arquivo arquivo = getArquivo(id);

        if (arquivo.getUsuario() != null
                && arquivo.getUsuario().getId().equals(moderador.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Moderadores não podem avaliar os próprios arquivos."
            );
        }

        String nomeAcao;

        if (novoStatus == StatusArquivo.APROVADO) {
            nomeAcao = "APROVACAO";
        } else if (novoStatus == StatusArquivo.REPROVADO) {
            nomeAcao = "REPROVACAO";
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status de avaliação inválido."
            );
        }

        arquivo.setStatus(novoStatus);
        arquivoRepository.save(arquivo);

        historicoService.registrarAcaoArquivo(
                nomeAcao,
                arquivo,
                moderador
        );

        if (novoStatus == StatusArquivo.REPROVADO) {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(arquivo.getNome_real_arquivo())
                    .build());
        }
    }

    public Arquivo getArquivo(int id){
        return arquivoRepository.findById(id).orElseThrow();
    }

    public Page<Arquivo> listarArquivos(int page){
        int size = 3;
        Pageable pageable = PageRequest.of(page, size, Sort.by("data").descending());
        return arquivoRepository.findByStatus(StatusArquivo.APROVADO, pageable);
    }

    public Page<Arquivo> listarArquivosUsuario(int id, int page){
        int size = 3;
        Pageable pageable = PageRequest.of(page, size);
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        return arquivoRepository.findByUsuario(usuario, pageable);
    }

    public Page<Arquivo> listarArquivosPendentes(int page){
        int size = 3;
        Pageable pageable = PageRequest.of(page, size);
        return arquivoRepository.findByStatus(StatusArquivo.PENDENTE, pageable);
    }

    public Page<Arquivo> listarArquivosPorDisciplina(String disciplina, StatusArquivo statusArquivo, int page){
        int size = 3;
        Pageable pageable = PageRequest.of(page, size);
        return arquivoRepository.findByDisciplinaAndStatus(disciplina, statusArquivo, pageable);
    }

}
