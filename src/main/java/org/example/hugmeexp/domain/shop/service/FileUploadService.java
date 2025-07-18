package org.example.hugmeexp.domain.shop.service;

import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.mission.enums.FileUploadType;
import org.example.hugmeexp.domain.mission.util.FileUploadUtils;
import org.example.hugmeexp.domain.shop.entity.Product;
import org.example.hugmeexp.domain.shop.entity.ProductImage;
import org.example.hugmeexp.domain.shop.exception.ImageUploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class FileUploadService {

    public ProductImage uploadProductImage(Product product, MultipartFile image) {
        try {
            String extension = getExtension(image.getOriginalFilename());
            String uploadDir = FileUploadUtils.getUploadPath(FileUploadType.PRODUCT_IMAGES).toString();
            Path uploadDirPath = Paths.get(uploadDir);

            if (!Files.exists(uploadDirPath)) {
                Files.createDirectories(uploadDirPath);
            }

            ProductImage productImage = product.registerProductImage("/application/" + FileUploadType.PRODUCT_IMAGES.value(), extension);
            String savedFileName = productImage.getUuid() + "." + productImage.getExtension();
            Path savePath = uploadDirPath.resolve(savedFileName);
            image.transferTo(savePath.toFile());

            log.info("File saved successfully: {}", savePath);
            return productImage;
        } catch (IOException e) {
            log.error("Error occurred while saving file", e);
            throw new ImageUploadException();
        }
    }

    private String getExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}