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
import org.springframework.web.multipart.MultipartFile;
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

    @Autowired
    private S3Client s3Client;

    @Autowired
    private ArquivoRepository arquivoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public void uploadFile(MultipartFile file, String nome, String disciplina, String descricao, Usuario usuario) throws IOException{
        String fileName = UUID.randomUUID() + "-"+ file.getOriginalFilename();

        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName).build(), RequestBody.fromBytes(file.getBytes()));
        String url = "https://"+bucket+".s3.amazonaws.com/"+fileName;
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

        arquivoRepository.save(arquivo);
    }

    public byte[] downloadFile(String key) {
        ResponseBytes<GetObjectResponse> objectAsBytes = s3Client.getObjectAsBytes(GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
        return objectAsBytes.asByteArray();
    }


    public void deletarArquivo(int id){
        Arquivo arquivo = arquivoRepository.findById(id).orElseThrow();
        String key = arquivo.getUrl().substring(arquivo.getUrl().lastIndexOf("/") + 1);
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
        arquivoRepository.delete(arquivo);

    }

    public Arquivo getArquivo(int id){
        return arquivoRepository.findById(id).orElseThrow();
    }

    public List<Arquivo> listarArquivos(){
        return arquivoRepository.findByStatus(StatusArquivo.APROVADO);
    }

    public List<Arquivo> listarArquivosUsuario(int id){
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        return arquivoRepository.findByUsuario(usuario);
    }

    public List<Arquivo> listarArquivosPendentes(){
        return arquivoRepository.findByStatus(StatusArquivo.PENDENTE);
    }

}
