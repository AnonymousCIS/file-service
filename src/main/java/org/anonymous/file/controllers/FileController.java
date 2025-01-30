package org.anonymous.file.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.anonymous.file.constants.FileStatus;
import org.anonymous.file.entities.FileInfo;
import org.anonymous.file.services.*;
import org.anonymous.global.exceptions.BadRequestException;
import org.anonymous.global.libs.Utils;
import org.anonymous.global.rests.JSONData;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

@Tag(name="파일 API", description = "파일 업로드, 조회, 다운로드, 삭제 기능 제공.")
@RestController
@RequiredArgsConstructor
public class FileController {

    private final Utils utils;
    private final FileUploadService uploadService;
    private final FileDownloadService downloadService;
    private final FileInfoService infoService;
    private final FileDeleteService deleteService;
    private final FileDoneService doneService;
    private final ThumbnailService thumbnailService;
    private final FileImageService imageService;

    /**
     * 파일 업로드
     *
     */
    @Operation(summary = "파일 업로드 처리")
    @ApiResponse(responseCode = "201", description = "파일 업로드 성공시에는 업로드 완료된 파일 목록이 반환됩니다. 요청시 반드시 요청헤더에 multipart/form-data 형식으로 전송")
    @Parameters({
            @Parameter(name="file", required = true,  description = "업로드할 파일 목록"),
            @Parameter(name="gid", required = true, description = "그룹 ID"),
            @Parameter(name="location", example="editor", description = "파일 구분 위치"),
            @Parameter(name="imageOnly", example="true", description = "이미지만 업로드 허용 여부"),
            @Parameter(name="single", example="true", description = "단일 파일 업로드 여부"),
            @Parameter(name="done", example="true", description = "업로드 하자마자 그룹 작업 완료 처리")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/upload")
    public JSONData upload(@RequestPart("file") MultipartFile[] files, @Valid RequestUpload form, Errors errors) {
        if (errors.hasErrors()) {
            throw new BadRequestException(utils.getErrorMessages(errors));
        }

        form.setFiles(files);

        /**
         * 단일 파일 업로드
         *      - 기 업로드된 파일을 삭제하고 새로 추가
         */
        if (form.isSingle()) {
            deleteService.deletes(form.getGid(), form.getLocation());
        }

        List<FileInfo> uploadedFiles = uploadService.upload(form);

        // 업로드 완료 하자마자 완료 처리
        if (form.isDone()) {
            doneService.process(form.getGid(), form.getLocation());
        }

        JSONData data = new JSONData(uploadedFiles);
        data.setStatus(HttpStatus.CREATED);

        return data;
    }

    // 파일 다운로드 처리
    @Operation(summary = "파일 다운로드 처리")
    @ApiResponse(responseCode = "200")
    @Parameter(name="seq", required = true, description = "경로변수, 파일 등록번호")

    @GetMapping("/download/{seq}")
    public void download(@PathVariable("seq") Long seq) {
        downloadService.process(seq);
    }

    // 파일 정보 단일 조회
    @Operation(summary = "파일 정보 단일 조회")
    @ApiResponse(responseCode = "200")
    @Parameter(name="seq", required = true, description = "경로변수, 파일 등록번호")

    @GetMapping("/view/{seq}")
    public JSONData info(@PathVariable("seq") Long seq) {
        FileInfo item = infoService.get(seq);

        return new JSONData(item);
    }

    /**
     * 파일 목록 조회
     * gid, location
     */
    @Operation(summary = "파일 목록 조회 - gid(그룹ID), location")
    @ApiResponse(responseCode = "200", description = "그룹 ID(gid)와 파일 구분 위치(location)으로 파일 목록 조회, location은 gid에 종속되는 검색 조건")
    @Parameters({
            @Parameter(name="gid", required = true, description = "경로변수, 그룹 ID"),
            @Parameter(name="location", description = "파일 구분 위치")
    })

    @GetMapping(path={"/list/{gid}/{location}"})
    public JSONData list(@PathVariable("gid") String gid,
                         @PathVariable(name="location", required = false) String location,
                         @RequestParam(name="status", defaultValue = "DONE") FileStatus status) {

        List<FileInfo> items = infoService.getList(gid, location, status);

        return new JSONData(items);
    }

    // 파일 단일 삭제
    @Operation(summary = "파일 단일 삭제")
    @ApiResponse(responseCode = "200", description = "파일 삭제 완료 후 삭제된 파일 정보 반환")
    @Parameter(name="seq", required = true, description = "경로변수, 파일 등록번호")

    @DeleteMapping("/delete/{seq}")
    public JSONData delete(@PathVariable("seq") Long seq) {

        FileInfo item = deleteService.delete(seq);

        return new JSONData(item);
    }

    // 파일 목록 삭제
    @Operation(summary = "파일 목록 삭제 - gid(그룹ID), location")
    @ApiResponse(responseCode = "200", description = "삭제 완료된 파일 목록 반환")
    @Parameters({
            @Parameter(name="gid", required = true, description = "경로변수, 그룹 ID"),
            @Parameter(name="location", description = "파일 구분 위치")
    })

    @DeleteMapping({"/deletes/{gid}", "/deletes/{gid}/{location}"})
    public JSONData deletes(@PathVariable("gid") String gid,
                            @PathVariable(name="location", required = false) String location) {

        List<FileInfo> items = deleteService.deletes(gid, location);

        return new JSONData(items);
    }


    // 썸네일 이미지 생성 처리
    @Operation(summary = "썸네일 생성")
    @ApiResponse(responseCode = "200", description = "생성된 이미지 출력")
    @Parameters({
            @Parameter(name="seq", required = true, description = "파일 등록번호 - seq, url 둘중 하나는 필수"),
            @Parameter(name="url", required = true, description = "파일 URL - seq, url 둘중 하나는 필수"),
            @Parameter(name="width", description = "생성될 이미지 너비, 값이 없다면 기본값 50px로 지정됨"),
            @Parameter(name="height", description = "생성될 이미지 높이, 값이 없다면 기본값 50px로 지정됨")
    })

    @GetMapping("/thumb")
    public void thumb(RequestThumb form, HttpServletResponse response) {
        String path = thumbnailService.create(form);
        if (!StringUtils.hasText(path)) {
            return;
        }

        File file = new File(path);
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            String contentType = Files.probeContentType(file.toPath());
            response.setContentType(contentType);

            OutputStream out = response.getOutputStream();
            out.write(bis.readAllBytes());

        } catch (IOException e) {}
    }


    // 파일 업로드시 노출되는 이미지 선택 처리
    @Operation(summary = "파일 업로드시 노출되는 이미지 선택 처리", description = "")

    @GetMapping("/select/{seq}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void select(@PathVariable("seq") Long seq) {
        imageService.select(seq);
    }

    /**
     * 파일 그룹작업 완료 처리
     *
     * @param gid
     * @param location
     */
    @Operation(summary = "파일 그룹 작업 완료 처리", method = "GET")
    @ApiResponse(responseCode = "200")
    @Parameters({
            @Parameter(name="gid", required = true, description = "경로변수, 그룹 ID"),
            @Parameter(name="location", description = "파일 그룹내 위치", example = "editor")
    })

    @GetMapping("/done/{gid}")
    public void processDone(@PathVariable("gid") String gid, @RequestParam(name = "location", required = false) String location) {
        doneService.process(gid, location);
    }

}
