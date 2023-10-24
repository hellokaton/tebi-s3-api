package com.example.tebis3api;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Data
@Configuration
@ConfigurationProperties(prefix = "r2")
public class AWSConfig {

    private String accessKey;

    private String secretKey;

    private String endpointUrl;

    private String bucket;

    private String region;

    private AWSCredentials credentials;

    @PostConstruct
    public void init() {
        try {
            credentials = new BasicAWSCredentials(accessKey, secretKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AmazonS3 getS3Client() {
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpointUrl, region))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .enablePathStyleAccess()
                .build();
    }

}
