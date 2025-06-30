package com.api.hub.chatbot.daoImpl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.api.hub.chatbot.entity.SocialMediaEntity;
import com.api.hub.chatbot.pojo.ChatBotException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;

@Repository
@Transactional
public class SocialMediaDao {

    @PersistenceContext
    private EntityManager em;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = ChatBotException.class)
    public void saveSocialMedia(SocialMediaEntity socialMedia) throws ChatBotException {
        try {
            em.persist(socialMedia);
        } catch (PersistenceException e) {
            throw new ChatBotException("Error saving Social Media entity", e.getMessage(), 500);
        }
    }

    public SocialMediaEntity getSocialMediaById(String appId) throws ChatBotException {
        try {
            SocialMediaEntity socialMedia = em.find(SocialMediaEntity.class, appId);
            if (socialMedia == null) {
                throw new ChatBotException("Social Media entity not found", "No entity found with appId: " + appId, 404);
            }
            return socialMedia;
        } catch (PersistenceException e) {
            throw new ChatBotException("Error fetching Social Media entity", e.getMessage(), 500);
        }
    }

    public List<SocialMediaEntity> getAllSocialMedia() throws ChatBotException {
        try {
            return em.createQuery("FROM SocialMediaEntity", SocialMediaEntity.class).getResultList();
        } catch (PersistenceException e) {
            throw new ChatBotException("Error fetching Social Media entities", e.getMessage(), 500);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ChatBotException.class)
    public void updateSocialMedia(SocialMediaEntity socialMedia) throws ChatBotException {
        try {
            em.merge(socialMedia);
        } catch (PersistenceException e) {
            throw new ChatBotException("Error updating Social Media entity", e.getMessage(), 500);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ChatBotException.class)
    public void deleteSocialMedia(String appId) throws ChatBotException {
        try {
            SocialMediaEntity socialMedia = em.find(SocialMediaEntity.class, appId);
            if (socialMedia != null) {
                em.remove(socialMedia);
            } else {
                throw new ChatBotException("Social Media entity not found", "No entity found with appId: " + appId, 404);
            }
        } catch (PersistenceException e) {
            throw new ChatBotException("Error deleting Social Media entity", e.getMessage(), 500);
        }
    }
}