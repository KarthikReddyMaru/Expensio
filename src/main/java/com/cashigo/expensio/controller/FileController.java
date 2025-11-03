package com.cashigo.expensio.controller;

import com.cashigo.expensio.service.file.FileStorageService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.OutputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final FileStorageService fileStorageService;

    @GetMapping("download/{fileId}")
    public void getFileById(HttpServletResponse response, @PathVariable String fileId) throws IOException {
        try(OutputStream outputStream = response.getOutputStream()) {
            String originalFileName = fileStorageService.getFileById(fileId, outputStream);
            String fileName = String.format("%s", originalFileName);
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename="+fileName);
        }
    }

}
