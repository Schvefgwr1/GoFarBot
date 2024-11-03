package com.example.gofarbot.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dialog_states")
public class DialogState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DialogStates state;

    public enum DialogStates {
        FIRST_MESSAGE, REGISTRATION, INFORMATION, CONSULTATION, GO_LEARN, CONTACTS, GUIDE, HANDLE
    }
}
