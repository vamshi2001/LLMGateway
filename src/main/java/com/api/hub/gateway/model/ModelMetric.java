package com.api.hub.gateway.model;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.Data;


public class ModelMetric {
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	public ReentrantReadWriteLock getLock() {
		return lock;
	}
	
	private AtomicLong currentActiveRequest = new AtomicLong(0);

    public AtomicLong getCurrentActiveRequest() {
		return currentActiveRequest;
	}

	public void setCurrentActiveRequest(long count) {
		currentActiveRequest.addAndGet(count);
	}

	private AtomicReference<Date> currentDate = new AtomicReference<Date>();
    private String modelId;

    private final AtomicLong currentInputTokenConsumedPerDay = new AtomicLong(0);
    private final AtomicLong currentInputTokenConsumedPerMonth = new AtomicLong(0);

    private final AtomicLong currentOutputTokenConsumedPerDay = new AtomicLong(0);
    private final AtomicLong currentOutputTokenConsumedPerMonth = new AtomicLong(0);

    private final AtomicLong requestPerDay = new AtomicLong(0);
    private final AtomicLong requestPerMonth = new AtomicLong(0);

    private final AtomicLong totalFailuresToday = new AtomicLong(0);
    private final AtomicLong failureInterval = new AtomicLong(0);

	// Getters — no locking, return values only
    public AtomicReference<Date> getCurrentDate() {
        return currentDate;
    }

    public String getModelId() {
        return modelId;
    }
    
    public AtomicLong getCurrentInputTokenConsumedPerDay() { return currentInputTokenConsumedPerDay; }
    public AtomicLong getCurrentInputTokenConsumedPerMonth() { return currentInputTokenConsumedPerMonth; }
    public AtomicLong getCurrentOutputTokenConsumedPerDay() { return currentOutputTokenConsumedPerDay; }
    public AtomicLong getCurrentOutputTokenConsumedPerMonth() { return currentOutputTokenConsumedPerMonth; }
    public AtomicLong getRequestPerDay() { return requestPerDay; }
    public AtomicLong getRequestPerMonth() { return requestPerMonth; }
    public AtomicLong getTotalFailuresToday() { return totalFailuresToday; }
    public AtomicLong getFailureInterval() { return failureInterval; }

    // Setters — with read lock (as requested)
    public void setCurrentDate(Date currentDate) {
        lock.readLock().lock();
        try {
            this.currentDate.set(currentDate);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setModelId(String modelId) {
        lock.readLock().lock();
        try {
            this.modelId = modelId;
        } finally {
            lock.readLock().unlock();
        }
    }

    public long setCurrentInputTokenConsumedPerDay(long value) {
        lock.readLock().lock();
        long var = 0;
        try {
        	var = this.currentInputTokenConsumedPerDay.addAndGet(value);
        } finally {
            lock.readLock().unlock();
        }
        return var;
    }

    public long setCurrentInputTokenConsumedPerMonth(long value) {
        lock.readLock().lock();
        long var = 0;
        try {
        	var =  this.currentInputTokenConsumedPerMonth.addAndGet(value);
        } finally {
            lock.readLock().unlock();
        }
        return var;
    }

    public long setCurrentOutputTokenConsumedPerDay(long value) {
        lock.readLock().lock();
        long var = 0;
        try {
        	var = this.currentOutputTokenConsumedPerDay.addAndGet(value);
        } finally {
            lock.readLock().unlock();
        }
        return var;
    }

    public long setCurrentOutputTokenConsumedPerMonth(long value) {
        lock.readLock().lock();
        long var = 0;
        try {
        	var = this.currentOutputTokenConsumedPerMonth.addAndGet(value);
        } finally {
            lock.readLock().unlock();
        }
        return var;
    }

    public long setRequestPerDay(long value) {
        lock.readLock().lock();
        long var = 0;
        try {
        	var = this.requestPerDay.addAndGet(value);
        } finally {
            lock.readLock().unlock();
        }
        return var;
    }

    public long setRequestPerMonth(long value) {
        lock.readLock().lock();
        long var = 0;
        try {
        	var = this.requestPerMonth.addAndGet(value);
        } finally {
            lock.readLock().unlock();
        }
        return var;
    }

    public long setTotalFailuresToday(long value) {
        lock.readLock().lock();
        long var = 0;
        try {
        	var = this.totalFailuresToday.addAndGet(value);
        } finally {
            lock.readLock().unlock();
        }
        return var;
    }

    public long setFailureInterval(long value) {
        lock.readLock().lock();
        long var = 0;
        try {
        	var = this.failureInterval.addAndGet(value);
        } finally {
            lock.readLock().unlock();
        }
        return var;
    }
    
}
