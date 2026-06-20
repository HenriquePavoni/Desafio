package com.atd.autorizacao.repository;

import com.atd.autorizacao.config.JPAUtil;
import com.atd.autorizacao.model.SolicitacaoAutorizacao;
import jakarta.persistence.EntityManager;

import java.util.List;

public class SolicitacaoRepository {

    public SolicitacaoAutorizacao salvar(SolicitacaoAutorizacao solicitacao) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(solicitacao);
            em.getTransaction().commit();
            return solicitacao;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<SolicitacaoAutorizacao> listarTodas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT s FROM SolicitacaoAutorizacao s ORDER BY s.dataSolicitacao DESC",
                            SolicitacaoAutorizacao.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
