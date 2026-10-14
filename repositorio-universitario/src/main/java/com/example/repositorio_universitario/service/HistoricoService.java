package com.example.repositorio_universitario.service;

import com.example.repositorio_universitario.Enums.FuncaoUsuario;
import com.example.repositorio_universitario.domain.*;
import com.example.repositorio_universitario.dto.HistoricoDTO;
import com.example.repositorio_universitario.repository.AcaoRepository;
import com.example.repositorio_universitario.repository.HistoricoArquivoRepository;
import com.example.repositorio_universitario.repository.HistoricoUsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class HistoricoService {

    private final HistoricoArquivoRepository historicoArquivoRepository;
    private final HistoricoUsuarioRepository historicoUsuarioRepository;
    private final AcaoRepository acaoRepository;

    public HistoricoService(
            HistoricoArquivoRepository historicoArquivoRepository,
            HistoricoUsuarioRepository historicoUsuarioRepository,
            AcaoRepository acaoRepository) {
        this.historicoArquivoRepository = historicoArquivoRepository;
        this.historicoUsuarioRepository = historicoUsuarioRepository;
        this.acaoRepository = acaoRepository;
    }

    public Page<HistoricoDTO> buscarHistoricoPaginado(Usuario usuarioLogado, int page, int size) {
        Map<String, HistoricoDTO> atividades = new LinkedHashMap<>();

        if (usuarioLogado.getFuncao() == FuncaoUsuario.ADMINISTRADOR) {
            List<HistoricoArquivo> todosArquivos = historicoArquivoRepository.findAll();
            for (HistoricoArquivo historico : todosArquivos) {
                adicionarHistoricoArquivo(atividades, historico, historico.getResponsavel().getNome());
            }

            List<HistoricoUsuario> todosUsuarios = historicoUsuarioRepository.findAll();
            for (HistoricoUsuario historico : todosUsuarios) {
                HistoricoDTO dto = new HistoricoDTO(
                        historico.getData(),
                        historico.getAcao().getNome(),
                        historico.getUsuario().getNome(),
                        historico.getResponsavel().getNome()
                );
                atividades.put("U-" + historico.getId(), dto);
            }
        } else {
            // USUÁRIO COMUM / MODERADOR: Histórico pessoal
            List<HistoricoArquivo> acoesRealizadas = historicoArquivoRepository.findByResponsavel(usuarioLogado);
            for (HistoricoArquivo historico : acoesRealizadas) {
                adicionarHistoricoArquivo(atividades, historico, usuarioLogado.getNome());
            }

            List<HistoricoArquivo> arquivosDoUsuario = historicoArquivoRepository.findByArquivo_Usuario(usuarioLogado);
            Set<String> acoesDeStatus = Set.of("APROVACAO", "REPROVACAO");

            for (HistoricoArquivo historico : arquivosDoUsuario) {
                String nomeAcao = historico.getAcao().getNome();
                if (acoesDeStatus.contains(nomeAcao)) {
                    boolean foiOutraPessoa = !historico.getResponsavel().getId().equals(usuarioLogado.getId());
                    String nomeExibicaoResponsavel = foiOutraPessoa ? "Moderação" : historico.getResponsavel().getNome();
                    adicionarHistoricoArquivo(atividades, historico, nomeExibicaoResponsavel);
                }
            }

            List<HistoricoUsuario> acoesSobreUsuarios = historicoUsuarioRepository.findByResponsavel(usuarioLogado);
            for (HistoricoUsuario historico : acoesSobreUsuarios) {
                HistoricoDTO dto = new HistoricoDTO(
                        historico.getData(),
                        historico.getAcao().getNome(),
                        historico.getUsuario().getNome(),
                        historico.getResponsavel().getNome()
                );
                atividades.put("U-" + historico.getId(), dto);
            }
        }

        // Ordenação decrescente por data
        List<HistoricoDTO> todosResultados = new ArrayList<>(atividades.values());
        todosResultados.sort(Comparator.comparing(
                HistoricoDTO::data,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));

        // Lógica manual de fatiamento para simular Page do Spring
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), todosResultados.size());

        List<HistoricoDTO> subList = (start > todosResultados.size())
                ? List.of()
                : todosResultados.subList(start, end);

        return new PageImpl<>(subList, pageable, todosResultados.size());
    }

    private void adicionarHistoricoArquivo(
            Map<String, HistoricoDTO> atividades,
            HistoricoArquivo historico,
            String nomeResponsavelExibicao) {

        HistoricoDTO dto = new HistoricoDTO(
                historico.getData(),
                historico.getAcao().getNome(),
                historico.getNomeArquivo(),
                nomeResponsavelExibicao // Nome parametrizado para preservar o anonimato quando necessário
        );

        atividades.put("A-" + historico.getId(), dto);
    }

    public void registrarAcaoUsuario(String nomeAcao, Usuario usuarioAfetado, Usuario responsavel) {
        Acao acao = acaoRepository.findByNome(nomeAcao)
                .orElseThrow(() -> new RuntimeException("Ação não encontrada: " + nomeAcao));

        HistoricoUsuario historico = new HistoricoUsuario();
        historico.setData(LocalDateTime.now());
        historico.setAcao(acao);
        historico.setUsuario(usuarioAfetado);
        historico.setResponsavel(responsavel);

        historicoUsuarioRepository.save(historico);
    }

    public void registrarAcaoArquivo(String nomeAcao, Arquivo arquivo, Usuario responsavel) {
        Acao acao = acaoRepository.findByNome(nomeAcao)
                .orElseThrow(() -> new RuntimeException("Ação não encontrada: " + nomeAcao));

        HistoricoArquivo historico = new HistoricoArquivo();
        historico.setData(LocalDateTime.now());
        historico.setAcao(acao);
        historico.setArquivo(arquivo);
        historico.setNomeArquivo(arquivo.getNome());
        historico.setResponsavel(responsavel);

        historicoArquivoRepository.save(historico);
    }
}