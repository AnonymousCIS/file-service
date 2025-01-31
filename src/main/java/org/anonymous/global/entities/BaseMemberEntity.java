package org.anonymous.global.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseMemberEntity extends BaseEntity {
    // BaseEntity를 상속받음으로서 날짜와 시간도 자동으로 추가되게 하였음

    @CreatedBy
    @Column(length=60, updatable = false)
    // 수정되지 않게 updatable = false 로 지정
    private String createdBy;

    @LastModifiedBy
    @Column(length=60, insertable = false) // 추가될때가 아닌 수정될때 들어가야하기 때문에 insertable = false 로 지정
    private String modifiedBy;
}
