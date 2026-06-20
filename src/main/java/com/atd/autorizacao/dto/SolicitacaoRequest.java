package com.atd.autorizacao.dto;

import com.atd.autorizacao.model.Sexo;

public class SolicitacaoRequest {

    private final String codigoProcedimento;
    private final int idade;
    private final Sexo sexo;

    public SolicitacaoRequest(String codigoProcedimento, int idade, Sexo sexo) {
        this.codigoProcedimento = codigoProcedimento;
        this.idade = idade;
        this.sexo = sexo;
    }

    public String getCodigoProcedimento() {
        return codigoProcedimento;
    }

    public int getIdade() {
        return idade;
    }

    public Sexo getSexo() {
        return sexo;
    }
}
