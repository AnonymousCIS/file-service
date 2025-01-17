package org.anonymous.file.services;

import lombok.RequiredArgsConstructor;
import org.anonymous.file.constants.FileStatus;
import org.anonymous.file.entities.FileInfo;
import org.anonymous.file.repositories.FileInfoRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
public class FileImageService {

    private final FileInfoService infoService;

    private final FileInfoRepository repository;

    /**
     * 목록 노출 이미지 선택
     *
     * @param seq
     */
    public void select(Long seq) {

        FileInfo item = infoService.get(seq);

        String gid = item.getGid();

        String location = item.getLocation();
        
        List<FileInfo> items = infoService.getList(gid, location, FileStatus.ALL);
        
        // 같은 Gid 내에서 목록 노출 선택 이미지만 변환
        items.forEach(i -> i.setSelected(item.getSeq().equals(seq)));
        
        repository.saveAllAndFlush(items);
    }
}