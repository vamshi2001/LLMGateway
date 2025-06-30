package com.api.hub.chatbot.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;

@Entity
@Table(name = "SOCIAL_MEDIA")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialMediaEntity implements Serializable {

    @Id
    @Column(name = "APP_ID", nullable = false, unique = true)
    private String appId;

    @Column(name = "APP_NAME", nullable = false)
    private String appName;
}