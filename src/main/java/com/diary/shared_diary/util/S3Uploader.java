package com.diary.shared_diary.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    public String upload(MultipartFile file, String dirName) {
        String key = dirName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(new PutObjectRequest(bucket, key, file.getInputStream(), metadata));
            return key; // URL이 아니라 key를 반환
        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }
    }

    public String getPresignedUrl(String key) {
        Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
        return amazonS3.generatePresignedUrl(bucket, key, expiration).toString();
    }

}
