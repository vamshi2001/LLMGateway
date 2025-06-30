package com.api.hub.chatbot.daoImpl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.api.hub.chatbot.entity.OrganisationEntity;
import com.api.hub.chatbot.pojo.ChatBotException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;

@Repository
@Transactional
public class OrganisationDAO {

    @PersistenceContext
    private EntityManager em;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = ChatBotException.class)
    public void saveOrganisation(OrganisationEntity organisation) throws ChatBotException {
        try {
            em.persist(organisation);
        } catch (PersistenceException e) {
            throw new ChatBotException("Error saving Organisation", e.getMessage(), 204);
        }
    }

    public OrganisationEntity getOrganisationByTenantId(String tenantId) throws ChatBotException {
        try {
            return em.find(OrganisationEntity.class, tenantId);
        } catch (PersistenceException e) {
            throw new ChatBotException("Error fetching Organisation by tenantId", e.getMessage(), 201);
        }
    }

    public List<OrganisationEntity> getAllOrganisations() throws ChatBotException {
        try {
            return em.createQuery("FROM OrganisationEntity", OrganisationEntity.class).getResultList();
        } catch (PersistenceException e) {
            throw new ChatBotException("Error fetching all Organisations", e.getMessage(), 201);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ChatBotException.class)
    public void updateOrganisation(OrganisationEntity organisation) throws ChatBotException {
        try {
            em.merge(organisation);
        } catch (PersistenceException e) {
            throw new ChatBotException("Error updating Organisation", e.getMessage(), 202);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ChatBotException.class)
    public void deleteOrganisation(String tenantId) throws ChatBotException {
        try {
            OrganisationEntity organisation = em.find(OrganisationEntity.class, tenantId);
            if (organisation != null) {
                em.remove(organisation);
            } else {
                throw new ChatBotException("Organisation not found", "No organisation found with tenantId: " + tenantId, 404);
            }
        } catch (PersistenceException e) {
            throw new ChatBotException("Error deleting Organisation", e.getMessage(), 203);
        }
    }
}