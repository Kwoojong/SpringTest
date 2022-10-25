package com.example.demo.controller;

import com.example.demo.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.Channels;

import java.nio.channels.Pipe;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
public class TestController {

    private final FileService fileService;

    public TestController(FileService fileService) {
        this.fileService = fileService;
    }
//
//    @PostMapping(value = "/file")
//    public String test(@RequestParam String name,
//                       @RequestBody List<MultipartFile> files) throws IOException {
//        System.out.println("Controller");
//
//        System.out.println("files.size() = " + files.size());
//        System.out.println("files.get(0).getClass() = " + files.get(0).getClass());
//
//        MultipartFile file = files.get(0);
//
////        fileService.fileUpload(name, file);
//        HttpURLConnection conn = null;
//
//        if (!file.isEmpty()) {
//            byte[] audio_file = file.getBytes();
//            String contentType = file.getContentType();
//            String parameterName = file.getName();
//            Resource resource = file.getResource();
//            long size = file.getSize();
//            String originalFilename = file.getOriginalFilename();
//
//
//            System.out.println("audio_file = " + audio_file);
//
//            System.out.println("audio_file.getClass() = " + audio_file.getClass());
//            System.out.println("contentType = " + contentType);
//            System.out.println("parameterName = " + parameterName);
//            System.out.println("resource = " + resource);
//            System.out.println("size = " + size);
//            System.out.println("originalFilename = " + originalFilename);
//            System.out.println("  ");
//            System.out.println("uploadSuccess");
//
//            URL url = new URL("http://133.186.251.227:54332/api/v1/asr/transcribe");
//
//            conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            String boundary = UUID.randomUUID().toString();
//            System.out.println("boundary = " + boundary);
//            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
//            conn.setRequestProperty("Accept", "application/json");
//            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
//
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//            conn.setUseCaches(false);
//
//
//            String jsonInputString = "{\"audio_file\":\""+ Arrays.toString(audio_file) +"\"}";
//
//            try (OutputStream os = conn.getOutputStream()) {
//                PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8), true);
//
//                final String LINE = "\r\n";
//
//                writer.append("--").append(boundary).append(LINE);
//                writer.append("Content-Disposition: form-data; name=\"" + "audio_file" + "\"; filename=\"").append(file.getOriginalFilename()).append("\"").append(LINE);
//                writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(file.getOriginalFilename())).append(LINE);
//                writer.append("Content-Transfer-Encoding: binary").append(LINE);
//                writer.append(LINE);
//                writer.flush();
//
//
//                FileInputStream inputStream = (FileInputStream) file.getInputStream();
//
////                byte[] buffer = new byte[4096];
////                int bytesRead = -1;
////                while ((bytesRead = inputStream.read(buffer)) != -1) {
////                    os.write(buffer, 0, bytesRead);
////                }
//                os.write(audio_file, 0 , audio_file.length);
//
//                os.flush();
//                inputStream.close();
//                writer.append(LINE);
//                writer.flush();
//
//            }catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            int status = conn.getResponseCode();
//
//            System.out.println("status = " + status);
//
//            try {
//                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
//                StringBuilder response = new StringBuilder();
//                String responseLine = null;
//
//                while ((responseLine = br.readLine()) != null) {
//                    response.append(responseLine.trim());
//                }
//                System.out.println(response.toString());
//                return response.toString();
//            }catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }else {
//            System.out.println("uploadFailure");
//        }
//
//        return null;
//    }


    @PostMapping("/file")
    public String test(@RequestParam String name,
                       @RequestBody List<MultipartFile> files) throws IOException, URISyntaxException, InterruptedException {

        MultipartFile multipartFile = files.get(0);

        File convFile = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(multipartFile.getBytes());
        fos.close();

        FileBody uploadFilePart = new FileBody(convFile);

        HttpEntity httpEntity = MultipartEntityBuilder.create()
                .addPart("audio_file", uploadFilePart)
                .build();

        /**
         * Use pipeline streams to write the encoded data directly to the network
         * instead of caching it in memory. Because Multipart request bodies contain
         * files, they can cause memory overflows if cached in memory.
         */
        Pipe pipe = Pipe.open();

        // Pipeline streams must be used in a multi-threaded environment. Using one
        // thread for simultaneous reads and writes can lead to deadlocks.
        new Thread(() -> {
            try (OutputStream outputStream = Channels.newOutputStream(pipe.sink())) {
                // Write the encoded data to the pipeline.
                httpEntity.writeTo(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder(new URI("http://133.186.251.227:54332/api/v1/asr/transcribe"))
                .version(HttpClient.Version.HTTP_1_1)
                // The Content-Type header is important, don't forget to set it.
                .header("Content-Type", httpEntity.getContentType().getValue())
                // Reads data from a pipeline stream.
                .POST(HttpRequest.BodyPublishers.ofInputStream(() -> Channels.newInputStream(pipe.source()))).build();

        HttpResponse<String> responseBody = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        System.out.println(responseBody.body());


        return (String) responseBody.body();
    }

    @PostMapping("/file2")
    public String file2(@RequestParam String name,
                        @RequestBody List<MultipartFile> files) throws IOException{

        return "{\"name\" : \""+ name +"\"}";
    }

    @GetMapping("/test")
    public String test2(@RequestParam String name) {
        System.out.println("테스트 완료");
        return name;
    }

    @PostMapping("/test2")
    public String test3(@RequestParam String name) {

        return "hello";
    }
}
