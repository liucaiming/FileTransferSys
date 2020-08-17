package org.example.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Arrays;

@RestController
@RequestMapping(value = "/qc")
public class FileManagerController {



    @Autowired
    private RestTemplate restTemplate;


    @GetMapping("/dc1")
    public void preInitResponseForFileStream1(HttpServletResponse response) {
        try {
            testDownLoadBigFile2(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void testDownLoadBigFile2(HttpServletResponse response) throws IOException {

        response.reset();
        //避免中文乱码
//        response.setHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes(), StandardCharsets.ISO_8859_1));
        response.setHeader("Content-Disposition", "attachment;filename=212.iso");

        response.setHeader("Connection", "close");
        //设置传输的类型
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Transfer-Encoding", "chunked");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/OCTET-STREAM");

        // 待下载的文件地址
        String url = "http://localhost:8081/file/download?fileName=212.iso";
        // 文件保存的本地路径
        String targetPath = "D:\\2121.iso";
        //定义请求头的接收类型
        RequestCallback requestCallback = request -> request.getHeaders()
                .setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
        //对响应进行流式处理而不是将其全部加载到内存中
        restTemplate.execute(url, HttpMethod.GET, requestCallback, clientHttpResponse -> {
            try (BufferedInputStream in = new BufferedInputStream(clientHttpResponse.getBody());
                 BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream())) {
                byte dataBuffer[] = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    out.write(dataBuffer, 0, bytesRead);
                }
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

}
