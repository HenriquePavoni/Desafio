package com.atd.autorizacao.dto;

import com.atd.autorizacao.model.StatusAutorizacao;

public class AutorizacaoResponse {

    private final StatusAutorizacao status;
    private final String motivo;

    public AutorizacaoResponse(StatusAutorizacao status, String motivo) {
        this.status = status;
        this.motivo = motivo;
    }

    public StatusAutorizacao getStatus() {
        return status;
    }

    public String getMotivo() {
        return motivo;
    }

    public boolean isAutorizado() {
        return status == StatusAutorizacao.AUTORIZADO;
    }
}
