package com.api.hub.gateway.model;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ModelMetricDTO {
    private String modelId;
    private Date currentDate;
    private String currentDateStr;
    
    private long currentActiveRequest;

    private long currentInputTokenConsumedPerDay;
    private long currentInputTokenConsumedPerMonth;

    private long currentOutputTokenConsumedPerDay;
    private long currentOutputTokenConsumedPerMonth;

    private long requestPerDay;
    private long requestPerMonth;

    private long totalFailuresToday;
    private long failureInterval;
    
    
    public static ModelMetricDTO toDTO(ModelMetric modelMetric) {
        ModelMetricDTO dto = new ModelMetricDTO();

        dto.setModelId(modelMetric.getModelId());
        dto.setCurrentDate(modelMetric.getCurrentDate().get());

        dto.setCurrentActiveRequest(modelMetric.getCurrentActiveRequest().get());

        dto.setCurrentInputTokenConsumedPerDay(modelMetric.getCurrentInputTokenConsumedPerDay().get());
        dto.setCurrentInputTokenConsumedPerMonth(modelMetric.getCurrentInputTokenConsumedPerMonth().get());

        dto.setCurrentOutputTokenConsumedPerDay(modelMetric.getCurrentOutputTokenConsumedPerDay().get());
        dto.setCurrentOutputTokenConsumedPerMonth(modelMetric.getCurrentOutputTokenConsumedPerMonth().get());

        dto.setRequestPerDay(modelMetric.getRequestPerDay().get());
        dto.setRequestPerMonth(modelMetric.getRequestPerMonth().get());

        dto.setTotalFailuresToday(modelMetric.getTotalFailuresToday().get());
        dto.setFailureInterval(modelMetric.getFailureInterval().get());

        return dto;
    }
    
    public static ModelMetric toModelMetricO(ModelMetricDTO dto) {
    	ModelMetric modelMetric = new ModelMetric();
        modelMetric.setModelId(dto.getModelId());
        modelMetric.setCurrentDate(dto.getCurrentDate());

        modelMetric.setCurrentActiveRequest(dto.getCurrentActiveRequest());

        modelMetric.setCurrentInputTokenConsumedPerDay(dto.getCurrentInputTokenConsumedPerDay());
        modelMetric.setCurrentInputTokenConsumedPerMonth(dto.getCurrentInputTokenConsumedPerMonth());

        modelMetric.setCurrentOutputTokenConsumedPerDay(dto.getCurrentOutputTokenConsumedPerDay());
        modelMetric.setCurrentOutputTokenConsumedPerMonth(dto.getCurrentOutputTokenConsumedPerMonth());

        modelMetric.setRequestPerDay(dto.getRequestPerDay());
        modelMetric.setRequestPerMonth(dto.getRequestPerMonth());

        modelMetric.setTotalFailuresToday(dto.getTotalFailuresToday());
        modelMetric.setFailureInterval(dto.getFailureInterval());
        return modelMetric;
    }
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    // Convert java.util.Date to String
    public static String dateToString(Date date) {
    	
    	return sdf.format(date);
    }

    // Convert String to java.util.Date
    public static Date stringToDate(String dateTimeString) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, FORMATTER);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
		
		currentDateStr = dateToString(currentDate);
	}

	public String getCurrentDateStr() {
		return currentDateStr;
	}

	public void setCurrentDateStr(String currentDateStr) {
		this.currentDateStr = currentDateStr;
		currentDate = stringToDate(currentDateStr);
	}

	public long getCurrentActiveRequest() {
		return currentActiveRequest;
	}

	public void setCurrentActiveRequest(long currentActiveRequest) {
		this.currentActiveRequest = currentActiveRequest;
	}

	public long getCurrentInputTokenConsumedPerDay() {
		return currentInputTokenConsumedPerDay;
	}

	public void setCurrentInputTokenConsumedPerDay(long currentInputTokenConsumedPerDay) {
		this.currentInputTokenConsumedPerDay = currentInputTokenConsumedPerDay;
	}

	public long getCurrentInputTokenConsumedPerMonth() {
		return currentInputTokenConsumedPerMonth;
	}

	public void setCurrentInputTokenConsumedPerMonth(long currentInputTokenConsumedPerMonth) {
		this.currentInputTokenConsumedPerMonth = currentInputTokenConsumedPerMonth;
	}

	public long getCurrentOutputTokenConsumedPerDay() {
		return currentOutputTokenConsumedPerDay;
	}

	public void setCurrentOutputTokenConsumedPerDay(long currentOutputTokenConsumedPerDay) {
		this.currentOutputTokenConsumedPerDay = currentOutputTokenConsumedPerDay;
	}

	public long getCurrentOutputTokenConsumedPerMonth() {
		return currentOutputTokenConsumedPerMonth;
	}

	public void setCurrentOutputTokenConsumedPerMonth(long currentOutputTokenConsumedPerMonth) {
		this.currentOutputTokenConsumedPerMonth = currentOutputTokenConsumedPerMonth;
	}

	public long getRequestPerDay() {
		return requestPerDay;
	}

	public void setRequestPerDay(long requestPerDay) {
		this.requestPerDay = requestPerDay;
	}

	public long getRequestPerMonth() {
		return requestPerMonth;
	}

	public void setRequestPerMonth(long requestPerMonth) {
		this.requestPerMonth = requestPerMonth;
	}

	public long getTotalFailuresToday() {
		return totalFailuresToday;
	}

	public void setTotalFailuresToday(long totalFailuresToday) {
		this.totalFailuresToday = totalFailuresToday;
	}

	public long getFailureInterval() {
		return failureInterval;
	}

	public void setFailureInterval(long failureInterval) {
		this.failureInterval = failureInterval;
	}

}
