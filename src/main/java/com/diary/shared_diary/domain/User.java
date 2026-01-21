package com.diary.shared_diary.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;

    @Column(columnDefinition = "TEXT")
    private String publicKey;

    private String refreshToken;

    private String authorizationCode;
    private LocalDateTime authorizationCodeExpiresAt;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "author")
    @JsonIgnore
    private List<Diary> diaries;

    @OneToMany(mappedBy = "author")
    @JsonIgnore
    private List<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    @Builder.Default
    private List<GroupMember> groupMembers = new ArrayList<>();


}
