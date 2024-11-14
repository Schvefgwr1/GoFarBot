package com.example.gofarbot.models;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    @ManyToOne
    @JoinColumn(name = "file")
    private File file;

    @NotNull
    private String code;

    @Nullable
    @Column(name = "next_message")
    private Long nextMessageId;

    @Nullable
    @ManyToMany(fetch = FetchType.EAGER, targetEntity = Button.class)
    @JoinTable(
            name = "buttons_messages_rel",
            joinColumns = {@JoinColumn(name = "message_id")},
            inverseJoinColumns = {@JoinColumn(name = "button_id")}
    )
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Button> buttons;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

