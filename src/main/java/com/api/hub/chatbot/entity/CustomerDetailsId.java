package com.api.hub.chatbot.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDetailsId implements Serializable {

    @Column(name = "CUSTOMER_ID", nullable = false)
    private Integer customerId;

    @Column(name = "TENENT_ID", nullable = false)
    private String tenantId;

    @Column(name = "APP_ID", nullable = false)
    private String appId;
}
