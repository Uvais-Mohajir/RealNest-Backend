package com.realnest.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceImplTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private CloudinaryServiceImpl cloudinaryService;

    @Test
    void uploadImage_shouldReturnEmpty_whenFileNull() {
        assertEquals("", cloudinaryService.uploadImage(null));
    }

    @Test
    void uploadImage_shouldReturnEmpty_whenFileEmpty() {
        MockMultipartFile file = new MockMultipartFile("image", new byte[0]);
        assertEquals("", cloudinaryService.uploadImage(file));
    }

    @Test
    void uploadImage_shouldReturnSecureUrl_whenUploadSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile("image", "a.jpg", "image/jpeg", "123".getBytes());
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(Map.of("secure_url", "https://cdn.example/a.jpg"));

        String result = cloudinaryService.uploadImage(file);

        assertEquals("https://cdn.example/a.jpg", result);
    }

    @Test
    void uploadImage_shouldThrowRuntime_whenUploadFails() throws Exception {
        MockMultipartFile file = new MockMultipartFile("image", "a.jpg", "image/jpeg", "123".getBytes());
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap())).thenThrow(new IOException("boom"));

        assertThrows(RuntimeException.class, () -> cloudinaryService.uploadImage(file));
    }
}
