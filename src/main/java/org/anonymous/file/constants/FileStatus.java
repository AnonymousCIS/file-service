package org.anonymous.file.constants;

public enum FileStatus {
    ALL, // 완료 + 미완료
    DONE, // 완료
    UNDONE, // 미완료 - 추후 스케쥴러가 DB 삭제할 파일 EX)30일 지나면 삭제
    BLOCK, // 숨김 처리 - 유저가 숨김(삭제)처리한 파일, 추후 관리자가 삭제 가능
}
