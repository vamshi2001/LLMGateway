package com.api.hub.chatbot.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatHistoryId implements Serializable {

    @Column(name = "CHAT_ID", nullable = false)
    private String chatId;

    @Temporal(TemporalType.DATE)
    @Column(name = "CHAT_TIME", nullable = false)
    private Date chatTime;
}