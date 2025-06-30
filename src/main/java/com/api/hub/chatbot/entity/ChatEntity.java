package com.api.hub.chatbot.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "CHAT")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatEntity implements Serializable {

	
    @Id
    @Column(name = "CHAT_ID", nullable = false)
    private String chatId;
    
    @Column(name = "CUSTOMER_ID", nullable = false)
    private Integer customerId;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE", nullable = false)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE", nullable = true)
    private Date endDate;

    //@ManyToOne
    //@JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "CUSTOMER_ID", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_CHAT_CUSTOMER_ID"))
    //@Transient
    //private CustomerDetailsEntity customerDetails;
}