package com.example.gofarbot.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "message_views", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"message_id", "user_chat_id"}))
@EntityListeners(AuditingEntityListener.class)
public class MessageView {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;
    
    @Column(name = "user_chat_id", nullable = false)
    private Long userChatId;
    
    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Integer viewCount = 1;
    
    @CreatedDate
    @Column(name = "first_viewed_at", nullable = false)
    private LocalDateTime firstViewedAt;
    
    @LastModifiedDate
    @Column(name = "last_viewed_at", nullable = false) 
    private LocalDateTime lastViewedAt;
} 