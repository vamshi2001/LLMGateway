package com.api.hub.chatbot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CUSTOMER_DETAILS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDetailsEntity {

    @EmbeddedId
    private CustomerDetailsId id;

    @Column(name = "APP_USER_ID", nullable = false)
    private String appUserId;

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Column(name = "COUNTRY_CODE", nullable = true)
    private Integer countryCode;

    @Column(name = "PHONE_NUMBER", nullable = true)
    private String phoneNumber;

    @Column(name = "EMAIL_ID", nullable = true)
    private String emailId;

    @Column(name = "ADDRESS", nullable = true)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TENENT_ID", referencedColumnName = "TENENT_ID", insertable = false, updatable = false)
    private OrganisationEntity organisation;

    @ManyToOne
    @JoinColumn(name = "APP_ID", referencedColumnName = "APP_ID", insertable = false, updatable = false)
    private SocialMediaEntity socialMedia;
}
