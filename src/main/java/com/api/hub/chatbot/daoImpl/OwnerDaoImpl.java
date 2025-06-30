package com.api.hub.chatbot.daoImpl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.api.hub.chatbot.entity.OwnerEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
@Transactional
public class OwnerDaoImpl {

    @PersistenceContext
    private EntityManager em;

    public void saveOwner(OwnerEntity owner) {
        em.persist(owner);
    }

    public OwnerEntity getOwnerByUsername(String username) {
        return em.find(OwnerEntity.class, username);
    }

    public List<OwnerEntity> getAllOwners() {
        return em.createQuery("FROM Owner", OwnerEntity.class).getResultList();
    }

    public void updateOwner(OwnerEntity owner) {
        em.merge(owner);
    }

    public void deleteOwner(String username) {
        OwnerEntity owner = em.find(OwnerEntity.class, username);
        if (owner != null) {
            em.remove(owner);
        }
    }
}