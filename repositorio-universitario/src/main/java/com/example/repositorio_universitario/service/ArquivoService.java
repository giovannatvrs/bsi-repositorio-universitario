package com.example.repositorio_universitario.service;


import com.example.repositorio_universitario.Enums.StatusArquivo;
import com.example.repositorio_universitario.domain.Arquivo;
import com.example.repositorio_universitario.domain.Usuario;
import com.example.repositorio_universitario.repository.ArquivoRepository;
import com.example.repositorio_universitario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.UUID;

@Service
public class ArquivoService {
    @Value("${aws.s3.bucket}")
    private String bucket;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private ArquivoRepository arquivoRepository;

    public void uploadFile(MultipartFile file, String disciplina, String descricao, Usuario usuario) throws IOException{
        String fileName = UUID.randomUUID() + "-"+ file.getOriginalFilename();

        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName).build(), RequestBody.fromBytes(file.getBytes()));
        String url = "https://"+bucket+".s3.amazonaws.com/"+fileName;
        Arquivo arquivo = new Arquivo();
        arquivo.setNome(file.getOriginalFilename());
        arquivo.setDisciplina(disciplina);
        arquivo.setData(LocalDateTime.now());
        arquivo.setUrl(url);
        arquivo.setDescricao(descricao);
        arquivo.setStatus(StatusArquivo.PENDENTE)
        arquivo.setUsuario(usuario);

        arquivoRepository.save(arquivo);
    }

    public ResponseEntity<Resource> download(int id){
        Arquivo arquivo = arquivoRepository.findById(id).orElseThrow();
        String key = arquivo.getUrl().substring(arquivo.getUrl().lastIndexOf("/") + 1);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> inputStream = s3Client.getObject(getObjectRequest);

        InputStreamResource resource = new InputStreamResource(inputStream);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + arquivo.getNome() + "\"")
                .body(resource);

    }

    public void deletarArquivo(int id){
        Arquivo arquivo = arquivoRepository.findById(id).orElseThrow();
        String key = arquivo.getUrl().substring(arquivo.getUrl().lastIndexOf("/") + 1);
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
    }

    public List<Arquivo> listarArquivos(){
        return arquivoRepository.findByStatus(StatusArquivo.APROVADO);
    }

    public List<Arquivo> listarArquivosUsuario(Usuario usuario){
        return arquivoRepository.findByUsuario(usuario);
    }

    public List<Arquivo> listarArquivosPendentes(){
        return arquivoRepository.findByStatus(StatusArquivo.PENDENTE);
    }

}
