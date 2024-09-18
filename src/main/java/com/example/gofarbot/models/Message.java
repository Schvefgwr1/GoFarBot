package com.example.gofarbot.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "messages")
@EntityListeners(AuditingEntityListener.class)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Integer number;

    @NotNull
    private String text;

    @ManyToOne
    @JoinColumn(name = "dialog")
    private DialogState dialog;

    @Nullable
    @ManyToMany(fetch = FetchType.EAGER, targetEntity = Link.class)
    @JoinTable(
            name = "messages_links_rel",
            joinColumns = {@JoinColumn(name = "message")},
            inverseJoinColumns = {@JoinColumn(name = "link")}
    )
    private List<Link> links;

    @Nullable
    @ManyToOne
    @JoinColumn(name = "file")
    private File file;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

