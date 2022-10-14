package com.example.demo.controller;

import com.example.demo.request.FileRequest;
import com.example.demo.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class TestController {

    private FileService fileService;

    @PostMapping("/file")
    public String test(FileRequest request) throws IOException {

        if (!request.getFile().isEmpty()) {
            byte[] bytes = request.getFile().getBytes();

            System.out.println("uploadSuccess");
        }else {
            System.out.println("uploadFailure");
        }

        return "file";
    }

    @PostMapping("/test")
    public String test2(@RequestParam String name) {

        return name;
    }
}
