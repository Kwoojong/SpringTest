package com.example.demo.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


@Setter
@Getter
public class FileRequest {

    private String name;
    private MultipartFile file;

}
