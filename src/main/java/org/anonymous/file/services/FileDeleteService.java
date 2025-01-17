package org.anonymous.file.services;

import lombok.RequiredArgsConstructor;
import org.anonymous.file.constants.FileStatus;
import org.anonymous.file.entities.FileInfo;
import org.anonymous.file.repositories.FileInfoRepository;
import org.anonymous.global.exceptions.UnAuthorizedException;
import org.anonymous.member.MemberUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.List;

/**
 * File 삭제 기능(Service)
 *
 */
@Lazy
@Service
@RequiredArgsConstructor
public class FileDeleteService {

    private final FileInfoService infoService;

    private final FileInfoRepository infoRepository;

    // 본인 File 여부 체크 (CreatedBy)
    private final MemberUtil memberUtil;

    // 삭제한 File 정보(Info)를 반환 값으로 내보내서 화면(Front)에서도 제거
    public FileInfo delete(Long seq) {

        // 삭제할 File
        FileInfo item = infoService.get(seq);

        String filePath = item.getFilePath();

        // 0. File 소유자만 삭제 가능하게 통제 - 다만 관리자는 모두 가능

        // null = 비회원 || 회원 Email
        String createdBy = item.getCreatedBy();

        // 관리자 아니고 && 비로그인이 아닌 로그인회원이 작성한 File 이고
        // && (비로그인 상태이거나 || 해당 File의 작성자(createdBy)가 아닐때)
        if (!memberUtil.isAdmin() && StringUtils.hasText(createdBy)
                && (!memberUtil.isLogin() || !memberUtil.getMember().getEmail().equals(createdBy))) {

            // 삭제 권한 X
            throw new UnAuthorizedException();
        }

        // 1. DB에서 정보 제거
        infoRepository.delete(item);
        infoRepository.flush();;

        // 2. File이 Server에 존재하면 File도 삭제
        File file = new File(filePath);

        if (file.exists() && file.isFile()) {

            file.delete();
        }

        // 3. 삭제된 File 정보를 반환
        return item;
    }

    public List<FileInfo> deletes(String gid, String location) {

        List<FileInfo> items = infoService.getList(gid, location, FileStatus.ALL);
        items.forEach(i -> delete(i.getSeq()));

        return items;
    }

    /**
     * 게시글 하나에 여러개의 File 이 gid-location 으로 있을 경우
     * gid 로만 삭제해서 일괄 삭제하는 등
     * 
     * @param gid
     * @return
     */
    public List<FileInfo> deletes(String gid) {

        return deletes(gid,null);
    }
}