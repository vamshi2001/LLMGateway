package com.api.hub.chatbot.daoImpl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.api.hub.chatbot.entity.ChatEntity;
import com.api.hub.chatbot.pojo.ChatBotException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;

@Repository
public class ChatDAO {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public boolean existsByChatEntityId(String chatId) throws ChatBotException {
        try {
            String query = "SELECT COUNT(c) FROM ChatEntity c WHERE c.chatId = :chatId";
            Long count = em.createQuery(query, Long.class)
                           .setParameter("chatId", chatId)
                           .getSingleResult();
            return count > 0;
        } catch (PersistenceException e) {
            throw new ChatBotException("Error checking chat entity existence", e.getMessage(), 201);
        }
    }
    
    @Transactional
    public String existsByCustomerId(Integer customerID) throws ChatBotException {
    	String query = "SELECT c.chatId FROM ChatEntity c " +
                "WHERE c.customerId = :customerId " +
                "AND c.endDate IS NULL " +
                "ORDER BY c.startDate DESC";

	    try {
	    	String chatId =  em.createQuery(query, String.class)
	              .setParameter("customerId", customerID)
	              .setMaxResults(1) // Ensures only one result (most recent)
	              .getSingleResult();
	    	return chatId;
        } catch (NoResultException e) {
            return null; // Return null if no matching record is found
        }catch (PersistenceException e) {
            throw new ChatBotException("Error checking chat entity existence", e.getMessage(), 201);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ChatBotException.class)
    public void saveChatEntity(ChatEntity chat) throws ChatBotException {
        try {
            em.persist(chat);
        } catch (PersistenceException e) {
            throw new ChatBotException("Error saving chat entity", e.getMessage(), 204);
        }
    }

    String jpql = """
            SELECT c, cd 
            FROM ChatEntity c 
            JOIN CustomerDetailsEntity cd 
            ON c.customerId = cd.id.customerId
            WHERE c.chatId = :chatId
        """;
    @Transactional(readOnly = true)
    public Object[] getChatEntityById(String chatId) throws ChatBotException {
        try {
        	return (Object[]) em.createQuery(jpql)
                    .setParameter("chatId", chatId)
                    .getSingleResult();
        } catch (PersistenceException e) {
            throw new ChatBotException("Error fetching chat entity by ID", e.getMessage(), 201);
        }
    }

    @Transactional(readOnly = true)
    public List<ChatEntity> getAllChatEntitys() throws ChatBotException {
        try {
            return em.createQuery("FROM ChatEntity", ChatEntity.class).getResultList();
        } catch (PersistenceException e) {
            throw new ChatBotException("Error fetching all chat entities", e.getMessage(), 201);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ChatBotException.class)
    public void updateChatEntity(ChatEntity chat) throws ChatBotException {
        try {
            em.merge(chat);
        } catch (PersistenceException e) {
            throw new ChatBotException("Error updating chat entity", e.getMessage(), 202);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ChatBotException.class)
    public void deleteChatEntity(ChatEntity chatInput) throws ChatBotException {
        try {
            ChatEntity chat = em.find(ChatEntity.class, chatInput);
            if (chat != null) {
                em.remove(chat);
            }
        } catch (PersistenceException e) {
            throw new ChatBotException("Error deleting chat entity", e.getMessage(), 203);
        }
    }
}