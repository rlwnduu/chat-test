package com.example.chat.file.service;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    public static final String ORIG = "./upload/profile/images/orig/";
    public static final String THUMB = "./upload/profile/images/thumb/";

    public String uploadProfileImage(MultipartFile profileImage) {
        if (profileImage == null || profileImage.isEmpty()) {
            return null;
        }

        String fileName = UUID.randomUUID() + ".jpg";

        String orig = ORIG + fileName;
        String thumb = THUMB + fileName;

        try {
            Thumbnails.of(profileImage.getInputStream())
                    .scale(1.0) // 크기 유지
                    .outputFormat("jpg") // 5. JPG로 강제 변환
                    .toFile(orig);

            Thumbnails.of(Path.of(orig).toFile())
                    .size(100, 100)
                    .outputFormat("jpg") // 7. JPG로 저장
                    .toFile(thumb);

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 또는 변환에 실패했습니다.", e);
        }
    }

    public Resource downloadThumb(String fileName) {
        try {
            return new UrlResource(THUMB + fileName);
        } catch (MalformedURLException e) {
            throw new RuntimeException("이미지 다운로드에 실패했습니다.", e);
        }
    }

    public Resource downloadOrig(String fileName) {
        try {
            return new UrlResource(ORIG + fileName);
        } catch (MalformedURLException e) {
            throw new RuntimeException("이미지 다운로드에 실패했습니다.", e);
        }
    }
}
