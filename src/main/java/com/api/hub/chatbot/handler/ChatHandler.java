package com.api.hub.chatbot.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.hub.chatbot.cache.LocalCacheStorage;
import com.api.hub.chatbot.dao.BaseDao;
import com.api.hub.chatbot.daoImpl.ChatDAO;
import com.api.hub.chatbot.daoImpl.ChatHistoryDao;
import com.api.hub.chatbot.daoImpl.CustomerDetailsDAO;
import com.api.hub.chatbot.entity.BusinessEntity;
import com.api.hub.chatbot.entity.ChatEntity;
import com.api.hub.chatbot.entity.CustomerDetailsEntity;
import com.api.hub.chatbot.entity.CustomerDetailsId;
import com.api.hub.chatbot.integrations.LLMIntegration;
import com.api.hub.chatbot.pojo.Chat;
import com.api.hub.chatbot.pojo.ChatBotException;
import com.api.hub.chatbot.pojo.ChatDataHolder;
import com.api.hub.chatbot.pojo.ChatHistory;
import com.api.hub.chatbot.pojo.ChatMetaData;
import com.api.hub.chatbot.pojo.Constants.ChatFlow;
import com.api.hub.chatbot.pojo.CustomerDetails;
import com.api.hub.chatbot.pojo.Vector;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatHandler {
	
	private static final org.slf4j.Logger customLogger = org.slf4j.LoggerFactory.getLogger("chat_data");

	@Autowired
	@Qualifier("ollama")
	private LLMIntegration ollama;
	
	@Autowired
	@Qualifier("OpenApi")
	private LLMIntegration chatGpt;
	
	@Autowired
	private ChatDAO chatTable;
	
	@Autowired
	private CustomerDetailsDAO cdTable;
	
	@Autowired
	private ChatHistoryDao chatHistoryDao;
	
	@Autowired
	private LocalCacheStorage cache;
	
	@Autowired
	@Qualifier("BusinessData")
	private BaseDao<BusinessEntity> business;
	
	@Value("${nChatsToFetch}")
	private Integer nChats;

	public String isNewChat(String tenentId, String appUserId) throws ChatBotException {
		return cache.getChatIdFromCache(tenentId+"-"+appUserId);
				
	}
	
	public CustomerDetailsEntity saveUserDetails(String tenentId, Map<String,Object> socialMediaData) throws ChatBotException {
		CustomerDetailsEntity userAlreadyExists = cdTable.existsByAppIdAndAppUserId((String) socialMediaData.get("userId"), (String) socialMediaData.get("appId"));
		if(userAlreadyExists == null) {
			CustomerDetailsId id = new CustomerDetailsId();
			CustomerDetailsEntity details = new CustomerDetailsEntity();
			details.setFirstName((String) socialMediaData.get("firstName"));
			details.setLastName((String) socialMediaData.get("lastName"));
			id.setAppId((String) socialMediaData.get("appId"));
			id.setTenantId(tenentId);
			details.setId(id);
			details.setAppUserId((String) socialMediaData.get("userId"));
			
			details = cdTable.saveCustomerDetails(details);
			return null;
			
		}
		return userAlreadyExists;
	}
	
	@Transactional
	public String initChat(String tenentId, Map<String,Object> socialMediaData, CustomerDetailsEntity user) throws ChatBotException {
		String chatId = "";
		try {
			chatId = (String) socialMediaData.get("chatId");
			if(user == null) {
				user = cdTable.existsByAppIdAndAppUserId((String) socialMediaData.get("userId"), (String) socialMediaData.get("appId"));
				if(user == null) {
		            throw new ChatBotException("Error fetching Customer Details by appId and appUserId", "", 500);
				}
			}else {
                chatId = chatTable.existsByCustomerId(user.getId().getCustomerId());
                if(chatId != null) {
                	return chatId;
                }
			}
			
			ChatMetaData data = new ChatMetaData();
			data.setChatHistory(new ChatHistory());
			CustomerDetails cDetails = new CustomerDetails();
			cDetails.setFirstName((String) socialMediaData.get("firstName"));
			cDetails.setLastName((String) socialMediaData.get("lastName"));
			cDetails.setSocialMediaData(socialMediaData);
			
			if( chatId != null && !chatId.isBlank()) {
				data.setChatId(chatId);
			}else {
				UUID uuid = UUID.randomUUID();
				chatId = uuid.toString();
				data.setChatId(chatId);
			}
		
			cDetails.setCountryCode(user.getCountryCode());
			cDetails.setEmailID(user.getEmailId());
			cDetails.setPhoneNumber(user.getPhoneNumber());
			data.setCustomerId(user.getId().getCustomerId());
			
			data.setCustomerId(user.getId().getCustomerId());
			data.setCustomerDetails(cDetails);
			data.setUserName(cDetails.getFirstName() + " " + cDetails.getLastName());
			data.setAppId((String) socialMediaData.get("appId"));
			data.setTenentId(tenentId);
			data.setStartTime(new Date());
			
			ChatEntity chatInputData = new ChatEntity();
			chatInputData.setChatId(chatId);
			chatInputData.setCustomerId(data.getCustomerId());
			chatInputData.setStartDate(data.getStartTime());
		
			chatTable.saveChatEntity(chatInputData);
			cache.saveChatData(data, chatId);
			cache.setChatIdHolder(tenentId+"-"+((String) socialMediaData.get("userId")), chatId);
		} catch (ChatBotException e) {
			// TODO Auto-generated catch block
			throw e;
		}catch (Exception e) {
			// TODO: handle exception
			throw new ChatBotException("Error saving chat entity", e.getMessage(), 300);
		}
		return chatId;
	}

	public ChatMetaData getChatData(String tenentId, String chatId) throws ChatBotException {
		ChatMetaData data;
		try {
			data = cache.getChatData(chatId);
			if(data != null) {
				
				return data;
			}
			data = new ChatMetaData();
			Object[] arr = chatTable.getChatEntityById(chatId);
			CustomerDetailsEntity cDetails = (CustomerDetailsEntity) arr[1];
			ChatEntity chatdata = (ChatEntity) arr[0];
			data.setChatId(chatdata.getChatId());
			data.setAppId(cDetails.getId().getAppId());
			data.setTenentId(tenentId);
			data.setStartTime(chatdata.getStartDate());
			data.setEndTime(chatdata.getEndDate());
			data.setUserName(cDetails.getFirstName() + cDetails.getLastName());
			
			CustomerDetails cd = new CustomerDetails();
			
			cd.setCountryCode(cDetails.getCountryCode());
			cd.setEmailID(cDetails.getEmailId());
			cd.setFirstName(cDetails.getFirstName());
			cd.setLastName(cd.getLastName());
			cd.setPhoneNumber(cDetails.getPhoneNumber());
			
			Map<String,Object> socialMediadata = new HashMap<String, Object>();
			socialMediadata.put("appId", cDetails.getSocialMedia().getAppId());
			socialMediadata.put("source", cDetails.getSocialMedia().getAppName());
			socialMediadata.put("userId", cDetails.getAppUserId());
			cd.setSocialMediaData(socialMediadata);
			
			data.setCustomerDetails(cd);
			
			ChatHistory chatHistory = new ChatHistory();
			//get data from chat history
			List<com.api.hub.chatbot.entity.ChatHistoryEntity> history = chatHistoryDao.getChatHistoryById(chatId);
			history.forEach( e -> {
				Chat chat = new Chat(e.getQuery(), e.getResponse());
				chatHistory.saveChat(chat);
			});
			
			data.setChatHistory(chatHistory);
			cache.setChatIdHolder(tenentId+"-"+cDetails.getAppUserId(), chatId);
			cache.saveChatData(data, chatId);
		} catch (ChatBotException e) {
			// TODO Auto-generated catch block
			throw e;
		}catch (Exception e) {
			// TODO: handle exception
			throw new ChatBotException("Error saving chat entity", e.getMessage(), 300);
		}
		return data;
	}
	
	public String resloveUserQueryTask() throws ChatBotException {

		try {
			
			boolean nonBusinessQuestion = false;
			ChatMetaData data = ChatDataHolder.get();
			List<Chat> ChatHistory =  data.getChatHistory().getLastChats(nChats.intValue());
			customLogger.info("user query : " + data.getChatHistory().getLastChat().getQuery());
			BusinessEntity entity = new BusinessEntity();
			Vector vectorEmbedding;
			
			String refinedUserQuery = ollama.refinePrompt(ChatHistory);
			customLogger.info("refined query : " +refinedUserQuery);
			
			String categories = ollama.getCategories(refinedUserQuery).toLowerCase();
			customLogger.info("categories from query : " +categories);
			
			if(categories.equalsIgnoreCase("other")) {
				categories = "about";
				nonBusinessQuestion= true;
			}else {
				categories = "about," + categories;
			}
			entity.setCategory(categories);
			
			vectorEmbedding = ollama.getVector(refinedUserQuery);
			entity.setEmbedding(vectorEmbedding.getEmbedding());
			
			List<BusinessEntity> businessdataList = business.get(entity);
			
			String response = ollama.messageToUser(businessdataList, ChatHistory, nonBusinessQuestion);
			customLogger.info("response : " +response);
			return response;
		}catch (ChatBotException e) {
			throw e;
		}catch (Exception e) {
            throw new ChatBotException("unable to connect ollama", e.getMessage(), 500);
        }
		
	}

	public List<ChatFlow> getUserIntentions() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
