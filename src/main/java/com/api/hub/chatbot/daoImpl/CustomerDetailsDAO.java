package com.api.hub.chatbot.daoImpl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.api.hub.chatbot.entity.CustomerDetailsEntity;
import com.api.hub.chatbot.entity.CustomerDetailsId;
import com.api.hub.chatbot.pojo.ChatBotException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;

@Repository
public class CustomerDetailsDAO {

    @PersistenceContext
    private EntityManager em;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ChatBotException.class)
    public CustomerDetailsEntity existsByAppIdAndAppUserId(String appUserId, String appId) throws ChatBotException {
        String query = "SELECT cd FROM CustomerDetailsEntity cd WHERE cd.id.appId = :appId AND cd.appUserId = :appUserId";
        try {
            return em.createQuery(query, CustomerDetailsEntity.class)
                     .setParameter("appId", appId)
                     .setParameter("appUserId", appUserId)
                     .getSingleResult();
        } catch (NoResultException e) {
        	return null;
        }catch (PersistenceException e) {
            throw new ChatBotException("Error fetching Customer Details by appId and appUserId", e.getMessage(), 500);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = ChatBotException.class)
    public CustomerDetailsEntity saveCustomerDetails(CustomerDetailsEntity customerDetails) throws ChatBotException {
        try {
            em.persist(customerDetails);
            em.flush();  // Force database sync to retrieve generated ID
            return customerDetails;
        } catch (PersistenceException e) {
            throw new ChatBotException("Error saving Customer Details", e.getMessage(), 500);
        }
    }

    public CustomerDetailsEntity getCustomerDetailsById(CustomerDetailsId id) throws ChatBotException {
        try {
            return em.find(CustomerDetailsEntity.class, id);
        } catch (PersistenceException e) {
            throw new ChatBotException("Error fetching Customer Details by ID", e.getMessage(), 500);
        }
    }

    public List<CustomerDetailsEntity> getAllCustomerDetails() throws ChatBotException {
        try {
            return em.createQuery("FROM CustomerDetailsEntity", CustomerDetailsEntity.class).getResultList();
        } catch (PersistenceException e) {
            throw new ChatBotException("Error fetching all Customer Details", e.getMessage(), 500);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ChatBotException.class)
    public void updateCustomerDetails(CustomerDetailsEntity customerDetails) throws ChatBotException {
        try {
            em.merge(customerDetails);
        } catch (PersistenceException e) {
            throw new ChatBotException("Error updating Customer Details", e.getMessage(), 500);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ChatBotException.class)
    public void deleteCustomerDetails(CustomerDetailsId id) throws ChatBotException {
        try {
            CustomerDetailsEntity customerDetails = em.find(CustomerDetailsEntity.class, id);
            if (customerDetails != null) {
                em.remove(customerDetails);
            }
        } catch (PersistenceException e) {
            throw new ChatBotException("Error deleting Customer Details", e.getMessage(), 500);
        }
    }
}