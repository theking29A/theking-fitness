package com.theking.theking_backend.controller;

import com.theking.theking_backend.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/upload")
public class FileUploadController {

    @Value("${app.upload.path:/opt/app/uploads}")
    private String uploadPath;

    @Value("${app.upload.url-prefix:/uploads}")
    private String urlPrefix;

    @PostMapping
    public Result<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(400, "文件不能为空");
        }

        try {
            // 创建上传目录
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;

            // 保存文件
            Path filePath = uploadDir.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath);

            // 返回访问URL
            Map<String, String> result = new HashMap<>();
            result.put("url", urlPrefix + "/" + newFilename);
            result.put("filename", newFilename);
            result.put("originalName", originalFilename);

            return Result.success(result);
        } catch (IOException e) {
            return Result.error(500, "文件上传失败: " + e.getMessage());
        }
    }
}
