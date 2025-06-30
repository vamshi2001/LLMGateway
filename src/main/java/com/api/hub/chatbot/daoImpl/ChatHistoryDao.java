package com.api.hub.chatbot.daoImpl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.api.hub.chatbot.entity.ChatHistoryEntity;
import com.api.hub.chatbot.pojo.ChatBotException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

@Repository
public class ChatHistoryDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = ChatBotException.class)
    public ChatHistoryEntity saveChatHistory(ChatHistoryEntity chatHistory) throws ChatBotException {
        try {
            entityManager.persist(chatHistory);  // Auto-generates CHAT_ID
            return chatHistory;
        } catch (PersistenceException e) {
            throw new ChatBotException("Error saving chat history", e.getMessage(), 204);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = ChatBotException.class)
    public ChatHistoryEntity updateChatHistory(ChatHistoryEntity chatHistory) throws ChatBotException {
        try {
            return entityManager.merge(chatHistory);  // Handles both insert and update
        } catch (PersistenceException e) {
            throw new ChatBotException("Error updating chat history", e.getMessage(), 202);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = ChatBotException.class)
    public void deleteChatHistory(String chatId) throws ChatBotException {
        try {
            ChatHistoryEntity chatHistory = entityManager.find(ChatHistoryEntity.class, chatId);
            if (chatHistory != null) {
                entityManager.remove(chatHistory);
            }
        } catch (PersistenceException e) {
            throw new ChatBotException("Error deleting chat history", e.getMessage(), 203);
        }
    }

    public List<ChatHistoryEntity> getChatHistoryById(String chatId) throws ChatBotException {
        try {
        	TypedQuery<ChatHistoryEntity> query = entityManager.createQuery(
                    "SELECT ch FROM ChatHistoryEntity ch WHERE ch.chatHistoryId.chatId = :chatId",
                    ChatHistoryEntity.class
                );
                query.setParameter("chatId", chatId);
                return query.getResultList();
        } catch (PersistenceException e) {
            throw new ChatBotException("Error fetching chat history by ID", e.getMessage(), 201);
        }
    }
}