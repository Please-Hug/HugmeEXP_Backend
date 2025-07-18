package org.example.hugmeexp.domain.shop.service;

import org.example.hugmeexp.domain.shop.entity.Product;
import org.example.hugmeexp.domain.shop.entity.ProductImage;
import org.example.hugmeexp.domain.shop.exception.ImageUploadException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileUploadServiceTest {

    @InjectMocks
    private FileUploadService fileUploadService;

    @Test
    @DisplayName("이미지 업로드 실패 - IO 예외 발생")
    void uploadProductImage_fail_IOException() throws IOException {
        // given
        Product product = mock(Product.class);
        MultipartFile mockImage = mock(MultipartFile.class);

        when(mockImage.getOriginalFilename()).thenReturn("test.jpg");
        when(product.registerProductImage(any(), any())).thenReturn(mock(ProductImage.class));


        doThrow(new IOException("Disk is full")).when(mockImage).transferTo(any(File.class));

        // when & then
        assertThrows(ImageUploadException.class, () -> {
            fileUploadService.uploadProductImage(product, mockImage);
        });
    }
}