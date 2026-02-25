package com.realnest.service.impl;

import com.cloudinary.Cloudinary;
import com.realnest.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return "";
        }
        try {
            Map<?, ?> uploaded = cloudinary.uploader().upload(imageFile.getBytes(), Map.of());
            Object secureUrl = uploaded.get("secure_url");
            return secureUrl == null ? "" : secureUrl.toString();
        } catch (IOException e) {
            throw new RuntimeException("Unable to upload image to Cloudinary", e);
        }
    }
}
