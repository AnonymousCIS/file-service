package org.anonymous.file.controllers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestThumb {
    private Long seq;
    private String url; // 원격 이미지 URL / Long값의 seq이든 url이든 둘중에 하나는 있어야함
    private int width;
    private int height; // 너비와 높이를 정확하게 하는게 아니고 둘중에 큰걸 기준으로 삼음(이미지가 짤리는걸 방지하기 위해서 맞추는 작업)
}
