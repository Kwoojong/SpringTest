package com.example.demo.service;

import com.example.demo.request.FileRequest;
import com.example.demo.utils.HttpPostMultipart;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;


@Service
public class FileServiceImpl implements FileService {

    @Override
    public String fileUpload(String name, MultipartFile file) throws IOException {
        System.out.println("Service");


        try {
            // Set header
            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
            HttpPostMultipart multipart = new HttpPostMultipart("http://localhost/index", "utf-8", headers);
            // Add form field
            multipart.addFormField("username", "test_name");
            multipart.addFormField("password", "test_psw");
            // Add file
            multipart.addFilePart("imgFile", new File("/Users/apple/Desktop/test.png"));
            // Print result
            String response = multipart.finish();
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return name;
    }
}
