package com.ppopi.ppopihouse.global.infra.cloud;


import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageStorageService {

    private final Cloudinary cloudinary;

    public String upload(MultipartFile image) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    image.getBytes(),
                    Map.of(
                            "folder", "ppopi/diagnosis",
                            "public_id", UUID.randomUUID().toString()
                    )
            );

            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.");
        }
    }
}