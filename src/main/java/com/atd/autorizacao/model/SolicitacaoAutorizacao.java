package com.atd.autorizacao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacao_autorizacao")
public class SolicitacaoAutorizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_procedimento", nullable = false, length = 20)
    private String codigoProcedimento;

    @Column(name = "idade", nullable = false)
    private int idade;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo", nullable = false, length = 1)
    private Sexo sexo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusAutorizacao status;

    @Column(name = "motivo", length = 255)
    private String motivo;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;

    public SolicitacaoAutorizacao() {
    }

    public SolicitacaoAutorizacao(String codigoProcedimento, int idade, Sexo sexo,
                                  StatusAutorizacao status, String motivo, LocalDateTime dataSolicitacao) {
        this.codigoProcedimento = codigoProcedimento;
        this.idade = idade;
        this.sexo = sexo;
        this.status = status;
        this.motivo = motivo;
        this.dataSolicitacao = dataSolicitacao;
    }

    public Long getId() {
        return id;
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

    public StatusAutorizacao getStatus() {
        return status;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }
}
