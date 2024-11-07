package com.example.gofarbot.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "files")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String link;

    @Column(name = "file_id")
    @Null
    private String fileId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private FileType type;

    public enum FileType {
        PHOTO, DOCUMENT
    }
}
