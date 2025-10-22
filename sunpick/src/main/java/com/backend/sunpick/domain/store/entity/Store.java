package com.backend.sunpick.domain.store.entity;

import com.backend.sunpick.domain.member.entity.Member;
import com.backend.sunpick.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "store")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "owner_name", length = 6, nullable = false)
    private String ownerName;

    public void changeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("상점명은 비어있을 수 없습니다.");
        }
        this.name = name;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changeOwner(Member member) {
        if (member == null || member.getName() == null || member.getName().isBlank()) {
            throw new IllegalArgumentException("회원과 회원명은 비어있을 수 없습니다.");
        }
        this.member = member;
        this.ownerName = member.getName();
    }
}
