package org.anonymous.file.services;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.anonymous.file.entities.FileInfo;
import org.anonymous.file.exceptions.FileNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * File Download 기능(Service)
 *
 * File seq 로 접근해 Download
 *
 * Server 가 Client 에게 출력하는 흐름을 변경 하는
 * 응답 Header(Content-Disposition) 이용
 * 
 * Body 출력을 화면에 View 해주는 것이 아닌 File 로 변경
 *
 */
@Lazy
@Service
@RequiredArgsConstructor
public class FileDownloadService {

    // File 이 없으면 Download 하지 않고 back
    // File 이 있으면 이름(seq)로 가져와서 download 처리
    private final FileInfoService infoService;

    // 응답 Header 통제용
    private final HttpServletResponse response;

    public void process(Long seq) {

        // ★ fileName(원래 이름) + contentType ★
        FileInfo item = infoService.get(seq);

        String fileName = item.getFileName();

        // Window 는 fileName 한글이면 깨지는 문제 발생, DB는 3byte
        // 깨짐 방지로 2byte(Window)로 변환 처리
        fileName = new String(fileName.getBytes(), StandardCharsets.ISO_8859_1);

        String contentType = item.getContentType();

        // contentType Null 일 경우 default 값 후속 처리
        contentType = StringUtils.hasText(contentType) ? contentType : "application/octet-stream";

        File file = new File(item.getFilePath());

        // Download 요청 File 이 없을 경우 예외 처리
        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

        /* 응답 Header 통제 S */

        // Body 의 출력 흐름을 화면이 아닌 filename 에 지정된 file 로 변경
        response.setHeader("Content-Disposition", "attachment; filename = " + fileName);

        // Content-Type(파일 형식, img pdf 등등)을 파악해 파일 형식마다 다르게 처리하기 위해서
        response.setContentType(contentType);

        // Cache-Control 에서 cache 꺼서 항상 Server 에서 File Download 되도록
        // ★ 필수 옵션 ★
        response.setHeader("Cache-Control", "no-cache");
        
        // 옛날 Browser 용 Cache-Control
        // ★ 필수 옵션 ★
        response.setHeader("Pragma", "no-cache");

        // 큰 용량의 File Download 도중 Timeout 이 뜨지 않도록 만료시간 삭제 설정
        response.setIntHeader("Expires", 0);
        
        // File 용량
        response.setContentLengthLong(file.length());

        /* 응답 Header 통제 E */

        // ★ 응답 Body 출력 ★
        OutputStream out = response.getOutputStream();
        out.write(bis.readAllBytes());

        } catch (IOException e) {

            e.printStackTrace();
        }

        /*
        try {

            // Body 쪽 Data (File 내용)
            PrintWriter out = response.getWriter();
            out.println("test1");
            out.println("test2");
            out.println("test3");

        } catch (Exception e) {

        }
         */
    }
}