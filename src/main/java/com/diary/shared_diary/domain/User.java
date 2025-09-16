package com.diary.shared_diary.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
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

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "author")
    @JsonIgnore
    private List<Diary> diaries;

    @OneToMany(mappedBy = "author")
    @JsonIgnore
    private List<Comment> comments;

    @ManyToMany(mappedBy = "members")
    @JsonBackReference
    private List<Group> groups;

}
