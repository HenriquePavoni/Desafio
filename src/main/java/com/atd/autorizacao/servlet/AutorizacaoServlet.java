package com.atd.autorizacao.servlet;

import com.atd.autorizacao.dto.AutorizacaoResponse;
import com.atd.autorizacao.dto.SolicitacaoRequest;
import com.atd.autorizacao.model.Sexo;
import com.atd.autorizacao.repository.RegraAutorizacaoRepository;
import com.atd.autorizacao.repository.SolicitacaoRepository;
import com.atd.autorizacao.service.AutorizacaoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/autorizacoes")
public class AutorizacaoServlet extends HttpServlet {

    private static final String VIEW = "/WEB-INF/views/autorizacoes.jsp";
    private static final String ERRO_VALIDACAO =
            "Dados invalidos: informe procedimento, idade (numero) e sexo (M ou F).";

    private transient AutorizacaoService autorizacaoService;
    private transient SolicitacaoRepository solicitacaoRepository;

    public AutorizacaoServlet() {
    }

    AutorizacaoServlet(AutorizacaoService autorizacaoService, SolicitacaoRepository solicitacaoRepository) {
        this.autorizacaoService = autorizacaoService;
        this.solicitacaoRepository = solicitacaoRepository;
    }

    @Override
    public void init() {
        this.solicitacaoRepository = new SolicitacaoRepository();
        this.autorizacaoService = new AutorizacaoService(new RegraAutorizacaoRepository(), solicitacaoRepository);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("solicitacoes", solicitacaoRepository.listarTodas());
        request.getRequestDispatcher(VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            SolicitacaoRequest solicitacao = montarRequisicao(request);
            AutorizacaoResponse resultado = autorizacaoService.avaliar(solicitacao);
            request.setAttribute("resultado", resultado);
        } catch (IllegalArgumentException | NullPointerException e) {
            request.setAttribute("erro", ERRO_VALIDACAO);
        }
        request.setAttribute("solicitacoes", solicitacaoRepository.listarTodas());
        request.getRequestDispatcher(VIEW).forward(request, response);
    }

    private SolicitacaoRequest montarRequisicao(HttpServletRequest request) {
        String codigo = request.getParameter("codigoProcedimento");
        int idade = Integer.parseInt(request.getParameter("idade"));
        Sexo sexo = Sexo.valueOf(request.getParameter("sexo"));
        return new SolicitacaoRequest(codigo, idade, sexo);
    }
}
