package com.example.tebis3api;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

@RestController
@RequestMapping("tebi")
public class TebiController {

    private final AWSConfig awsConfig;

    @Autowired
    TebiController(AWSConfig awsConfig) {
        this.awsConfig = awsConfig;
    }

    @PostMapping(path = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String uploadFile(@RequestParam MultipartFile file) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        String key = IdUtil.simpleUUID() + "." + FileUtil.extName(file.getOriginalFilename());

        PutObjectRequest request = new PutObjectRequest(awsConfig.getBucket(), key, file.getInputStream(), metadata);
        request.setCannedAcl(CannedAccessControlList.PublicRead);
        // upload
        awsConfig.getS3Client().putObject(request);

        GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(awsConfig.getBucket(), key);
        URL url = awsConfig.getS3Client().generatePresignedUrl(urlRequest);
        return url.toString();
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadObject(@RequestParam String key) throws IOException {
        S3Object s3object = awsConfig.getS3Client().getObject(awsConfig.getBucket(), key);
        String contentType = s3object.getObjectMetadata().getContentType();
        S3ObjectInputStream stream = s3object.getObjectContent();

        String fileName = s3object.getKey().split("/")[s3object.getKey().split("/").length - 1];

        byte[] content = null;

        content = IOUtils.toByteArray(stream);
        s3object.close();

        final ByteArrayResource resource = new ByteArrayResource(content);
        return ResponseEntity
                .ok()
                .contentLength(content.length)
                .header("Content-type", contentType)
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

}

