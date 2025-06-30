package com.api.hub.chatbot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CHATHISTORY")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatHistoryEntity {

    @EmbeddedId
    private ChatHistoryId chatHistoryId;

    @Column(name = "QUERY", nullable = false)
    private String query;

    @Column(name = "RESPONSE", nullable = false)
    private String response;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHAT_ID", referencedColumnName = "CHAT_ID", insertable = false, updatable = false)
    private ChatEntity chat;
}
