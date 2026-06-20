package com.atd.autorizacao.service;

import com.atd.autorizacao.dto.AutorizacaoResponse;
import com.atd.autorizacao.dto.SolicitacaoRequest;
import com.atd.autorizacao.model.RegraAutorizacao;
import com.atd.autorizacao.model.SolicitacaoAutorizacao;
import com.atd.autorizacao.model.StatusAutorizacao;
import com.atd.autorizacao.repository.RegraAutorizacaoRepository;
import com.atd.autorizacao.repository.SolicitacaoRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public class AutorizacaoService {

    private final RegraAutorizacaoRepository regraRepository;
    private final SolicitacaoRepository solicitacaoRepository;

    public AutorizacaoService(RegraAutorizacaoRepository regraRepository,
                              SolicitacaoRepository solicitacaoRepository) {
        this.regraRepository = regraRepository;
        this.solicitacaoRepository = solicitacaoRepository;
    }

    public AutorizacaoResponse avaliar(SolicitacaoRequest request) {
        validar(request);

        String codigo = request.getCodigoProcedimento().trim();
        Optional<RegraAutorizacao> regra =
                regraRepository.buscarRegra(codigo, request.getIdade(), request.getSexo());

        StatusAutorizacao status;
        String motivo;
        if (regra.isPresent()) {
            if (regra.get().isPermitido()) {
                status = StatusAutorizacao.AUTORIZADO;
                motivo = "Procedimento autorizado conforme regra cadastrada para a idade e sexo informados.";
            } else {
                status = StatusAutorizacao.NEGADO;
                motivo = "Procedimento negado: a regra cadastrada nao permite a execucao para a idade e sexo informados.";
            }
        } else if (!regraRepository.existeProcedimento(codigo)) {
            status = StatusAutorizacao.NEGADO;
            motivo = "Procedimento negado: codigo nao cadastrado nas regras de autorizacao.";
        } else {
            status = StatusAutorizacao.NEGADO;
            motivo = "Procedimento negado: nao ha regra de autorizacao para a combinacao de idade e sexo informada.";
        }

        SolicitacaoAutorizacao solicitacao = new SolicitacaoAutorizacao(
                codigo, request.getIdade(), request.getSexo(), status, motivo, LocalDateTime.now());
        solicitacaoRepository.salvar(solicitacao);

        return new AutorizacaoResponse(status, motivo);
    }

    private void validar(SolicitacaoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("A solicitacao nao pode ser nula.");
        }
        if (request.getCodigoProcedimento() == null || request.getCodigoProcedimento().trim().isEmpty()) {
            throw new IllegalArgumentException("O codigo do procedimento e obrigatorio.");
        }
        if (request.getSexo() == null) {
            throw new IllegalArgumentException("O sexo e obrigatorio.");
        }
        if (request.getIdade() < 0) {
            throw new IllegalArgumentException("A idade nao pode ser negativa.");
        }
    }
}
