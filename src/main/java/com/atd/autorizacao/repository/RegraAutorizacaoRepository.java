package com.atd.autorizacao.repository;

import com.atd.autorizacao.config.JPAUtil;
import com.atd.autorizacao.model.RegraAutorizacao;
import com.atd.autorizacao.model.Sexo;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class RegraAutorizacaoRepository {

    public Optional<RegraAutorizacao> buscarRegra(String codigoProcedimento, int idade, Sexo sexo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<RegraAutorizacao> regras = em.createQuery(
                            "SELECT r FROM RegraAutorizacao r "
                                    + "WHERE r.codigoProcedimento = :codigo AND r.idade = :idade AND r.sexo = :sexo",
                            RegraAutorizacao.class)
                    .setParameter("codigo", codigoProcedimento)
                    .setParameter("idade", idade)
                    .setParameter("sexo", sexo)
                    .getResultList();
            return regras.stream().findFirst();
        } finally {
            em.close();
        }
    }

    public boolean existeProcedimento(String codigoProcedimento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long total = em.createQuery(
                            "SELECT COUNT(r) FROM RegraAutorizacao r WHERE r.codigoProcedimento = :codigo",
                            Long.class)
                    .setParameter("codigo", codigoProcedimento)
                    .getSingleResult();
            return total != null && total > 0;
        } finally {
            em.close();
        }
    }
}
