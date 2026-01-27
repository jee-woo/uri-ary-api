package com.diary.shared_diary.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diary {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String encryptedContent;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String iv;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String authTag;

    private LocalDateTime createdAt;

    @Column
    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL)
    private List<Comment> comments;


}
