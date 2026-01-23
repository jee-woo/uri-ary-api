package com.diary.shared_diary.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user_group")
@Getter
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



    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    @Builder.Default
    private List<GroupMember> groupMembers = new ArrayList<>();

    public void addMember(User user, MemberStatus status) {
        GroupMember groupMember = GroupMember.builder()
                .user(user)
                .group(this)
                .status(status)
                .joinedAt(LocalDateTime.now())
                .build();
        this.groupMembers.add(groupMember);
    }


    public void validateMember(User user) {
        boolean isMember = this.groupMembers.stream()
                .anyMatch(gm -> gm.getUser().equals(user) && gm.getStatus() == MemberStatus.ACCEPTED);

        if (!isMember) {
            throw new AccessDeniedException("그룹 승인된 멤버가 아닙니다.");
        }
    }
}
