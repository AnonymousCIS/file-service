package org.anonymous.member.constants;

/**
 * 회원 권한
 * // 파일 다운로드, 조회등 서비스이용시 권한 통제
 */
public enum Authority {
    ALL, // 모든 사용자(일반회원 + 최고 관리자(admin) + 비회원)
    USER, // 일반 회원

    ADMIN // 최고 관리자
}
