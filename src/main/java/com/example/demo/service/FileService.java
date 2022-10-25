package com.example.demo.service;

import com.example.demo.request.FileRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    public String fileUpload(String name, MultipartFile file) throws IOException;
}
