package org.anonymous.global.repositories;

import org.anonymous.global.entities.CodeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CodeValueRepository extends JpaRepository<CodeValue, String>, QuerydslPredicateExecutor<CodeValue> {
}
