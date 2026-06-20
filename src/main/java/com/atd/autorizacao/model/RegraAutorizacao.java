package com.atd.autorizacao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "regra_autorizacao")
public class RegraAutorizacao {

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

    @Column(name = "permitido", nullable = false)
    private boolean permitido;

    public RegraAutorizacao() {
    }

    public RegraAutorizacao(String codigoProcedimento, int idade, Sexo sexo, boolean permitido) {
        this.codigoProcedimento = codigoProcedimento;
        this.idade = idade;
        this.sexo = sexo;
        this.permitido = permitido;
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

    public boolean isPermitido() {
        return permitido;
    }
}
