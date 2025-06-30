package com.api.hub.chatbot.entity;
import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "ORGANISATION")
@Data
public class OrganisationEntity implements Serializable {

    @Id
    @Column(name = "TENENT_ID", nullable = false)
    private String tenantId;
    
    @Column(name = "USER_NAME", nullable = false, unique = true)
    private String userName;

    @Column(name = "ENTRY_DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date entryDate;

    @Column(name = "COMPANY_NAME", nullable = false)
    private String companyName;

    @Column(name = "EMAIL_ID", nullable = false)
    private String emailId;

    @Column(name = "ADDRESS", nullable = false)
    private String address;

    @ManyToOne
    @JoinColumn(name = "USER_NAME", referencedColumnName = "USER_NAME", nullable = false, insertable = false, updatable = false)
    private OwnerEntity owner;
}