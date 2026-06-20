package com.atd.autorizacao.service;

import com.atd.autorizacao.dto.AutorizacaoResponse;
import com.atd.autorizacao.dto.SolicitacaoRequest;
import com.atd.autorizacao.model.RegraAutorizacao;
import com.atd.autorizacao.model.Sexo;
import com.atd.autorizacao.model.SolicitacaoAutorizacao;
import com.atd.autorizacao.model.StatusAutorizacao;
import com.atd.autorizacao.repository.RegraAutorizacaoRepository;
import com.atd.autorizacao.repository.SolicitacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutorizacaoServiceTest {

    @Mock
    private RegraAutorizacaoRepository regraRepository;

    @Mock
    private SolicitacaoRepository solicitacaoRepository;

    @InjectMocks
    private AutorizacaoService service;

    @Test
    void deveAutorizarQuandoRegraCorrespondentePermite() {
        when(regraRepository.buscarRegra("4567", 20, Sexo.M))
                .thenReturn(Optional.of(new RegraAutorizacao("4567", 20, Sexo.M, true)));

        AutorizacaoResponse response = service.avaliar(new SolicitacaoRequest("4567", 20, Sexo.M));

        assertTrue(response.isAutorizado());
        assertEquals(StatusAutorizacao.AUTORIZADO, response.getStatus());
    }

    @Test
    void deveNegarQuandoRegraCorrespondenteNaoPermite() {
        when(regraRepository.buscarRegra("1234", 10, Sexo.M))
                .thenReturn(Optional.of(new RegraAutorizacao("1234", 10, Sexo.M, false)));

        AutorizacaoResponse response = service.avaliar(new SolicitacaoRequest("1234", 10, Sexo.M));

        assertFalse(response.isAutorizado());
        assertEquals(StatusAutorizacao.NEGADO, response.getStatus());
    }

    @Test
    void deveNegarQuandoProcedimentoNaoCadastrado() {
        when(regraRepository.buscarRegra("0000", 25, Sexo.F)).thenReturn(Optional.empty());
        when(regraRepository.existeProcedimento("0000")).thenReturn(false);

        AutorizacaoResponse response = service.avaliar(new SolicitacaoRequest("0000", 25, Sexo.F));

        assertFalse(response.isAutorizado());
        assertTrue(response.getMotivo().toLowerCase().contains("nao cadastrado"));
    }

    @Test
    void deveNegarQuandoProcedimentoExisteMasNaoHaRegraParaIdadeESexo() {
        when(regraRepository.buscarRegra("1234", 99, Sexo.F)).thenReturn(Optional.empty());
        when(regraRepository.existeProcedimento("1234")).thenReturn(true);

        AutorizacaoResponse response = service.avaliar(new SolicitacaoRequest("1234", 99, Sexo.F));

        assertFalse(response.isAutorizado());
        assertEquals(StatusAutorizacao.NEGADO, response.getStatus());
    }

    @Test
    void devePersistirSolicitacaoComStatusAvaliado() {
        when(regraRepository.buscarRegra("4567", 20, Sexo.M))
                .thenReturn(Optional.of(new RegraAutorizacao("4567", 20, Sexo.M, true)));

        service.avaliar(new SolicitacaoRequest("4567", 20, Sexo.M));

        ArgumentCaptor<SolicitacaoAutorizacao> captor = ArgumentCaptor.forClass(SolicitacaoAutorizacao.class);
        verify(solicitacaoRepository).salvar(captor.capture());
        SolicitacaoAutorizacao salva = captor.getValue();
        assertEquals(StatusAutorizacao.AUTORIZADO, salva.getStatus());
        assertEquals("4567", salva.getCodigoProcedimento());
        assertEquals(20, salva.getIdade());
        assertEquals(Sexo.M, salva.getSexo());
    }

    @Test
    void deveLancarExcecaoQuandoCodigoEmBranco() {
        assertThrows(IllegalArgumentException.class,
                () -> service.avaliar(new SolicitacaoRequest("  ", 20, Sexo.M)));
        verify(solicitacaoRepository, never()).salvar(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void deveLancarExcecaoQuandoSexoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> service.avaliar(new SolicitacaoRequest("1234", 20, null)));
    }

    @Test
    void deveLancarExcecaoQuandoIdadeNegativa() {
        assertThrows(IllegalArgumentException.class,
                () -> service.avaliar(new SolicitacaoRequest("1234", -1, Sexo.M)));
    }
}
