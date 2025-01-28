package org.anonymous.global.controllers;

import org.anonymous.file.repositories.FileInfoRepository;
import org.anonymous.file.services.FileDeleteService;
import org.anonymous.file.services.FileInfoService;
import org.anonymous.member.controllers.RequestJoin;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

@SpringBootTest
@ActiveProfiles({"default", "test"})
@AutoConfigureMockMvc
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileInfoRepository repository;

    @Autowired
    private FileInfoService infoService;

    @Autowired
    private FileDeleteService deleteService;

    @BeforeEach
    void setup() {

        RequestJoin form = new RequestJoin();
        form.setEmail("user01@test.org");
        form.setPassword("_aA123456");
        form.setBirthDt(LocalDate.now().minusYears(20));
        form.setName("이이름");
        form.setNickName("이이름");
        form.setZipCode("00000");
        form.setAddress("주소!");

        updateService.process(form);

    }

}
