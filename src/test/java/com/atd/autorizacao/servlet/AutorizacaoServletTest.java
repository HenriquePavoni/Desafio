package com.atd.autorizacao.servlet;

import com.atd.autorizacao.dto.AutorizacaoResponse;
import com.atd.autorizacao.model.StatusAutorizacao;
import com.atd.autorizacao.repository.SolicitacaoRepository;
import com.atd.autorizacao.service.AutorizacaoService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutorizacaoServletTest {

    @Mock
    private AutorizacaoService autorizacaoService;

    @Mock
    private SolicitacaoRepository solicitacaoRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    private AutorizacaoServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new AutorizacaoServlet(autorizacaoService, solicitacaoRepository);
        when(request.getRequestDispatcher(any())).thenReturn(dispatcher);
        when(solicitacaoRepository.listarTodas()).thenReturn(Collections.emptyList());
    }

    @Test
    void doPostDeveAvaliarEEncaminharResultadoQuandoDadosValidos() throws Exception {
        when(request.getParameter("codigoProcedimento")).thenReturn("4567");
        when(request.getParameter("idade")).thenReturn("20");
        when(request.getParameter("sexo")).thenReturn("M");
        AutorizacaoResponse resposta = new AutorizacaoResponse(StatusAutorizacao.AUTORIZADO, "ok");
        when(autorizacaoService.avaliar(any())).thenReturn(resposta);

        servlet.doPost(request, response);

        verify(request).setAttribute("resultado", resposta);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void doPostDeveRegistrarErroQuandoSexoInvalido() throws Exception {
        when(request.getParameter("codigoProcedimento")).thenReturn("4567");
        when(request.getParameter("idade")).thenReturn("20");
        when(request.getParameter("sexo")).thenReturn("X");

        servlet.doPost(request, response);

        verify(autorizacaoService, never()).avaliar(any());
        verify(request).setAttribute(eq("erro"), any());
        verify(dispatcher).forward(request, response);
    }

    @Test
    void doGetDeveListarSolicitacoesEEncaminhar() throws Exception {
        servlet.doGet(request, response);

        verify(request).setAttribute(eq("solicitacoes"), any());
        verify(dispatcher).forward(request, response);
    }
}
