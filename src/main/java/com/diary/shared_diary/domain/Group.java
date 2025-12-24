package com.diary.shared_diary.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(unique = true)
    private String code;

    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
            name = "group_user",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonManagedReference
    @Builder.Default
    private Set<User> members = new HashSet<>();

    public void addMember(User user) {
        this.members.add(user);
        user.getGroups().add(this);
    }

    public void validateMember(User user) {
        if (!this.members.contains(user)) {
            throw new AccessDeniedException("그룹 멤버가 아닙니다.");
        }
    }
}
