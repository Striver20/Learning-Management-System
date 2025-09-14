package com.example.lms.service;

import com.example.lms.dto.S3FileResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    @Getter
    private final String bucketName = "lms-springboot-s3";

    public S3FileResponse uploadFile(MultipartFile file) {
        try {
            String key = "uploads/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            String fileUrl = s3Client.utilities()
                    .getUrl(b -> b.bucket(bucketName).key(key))
                    .toExternalForm();

            return new S3FileResponse(fileUrl, key);

        } catch (IOException | S3Exception e) {
            throw new RuntimeException("S3 upload failed: " + e.getMessage(), e);
        }
    }


    public void deleteFile(String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

        } catch (S3Exception e) {
            throw new RuntimeException("S3 delete failed: " + e.getMessage(), e);
        }
    }

    public String getRegion() {
        return "ap-southeast-2"; // or inject from properties
    }

}
