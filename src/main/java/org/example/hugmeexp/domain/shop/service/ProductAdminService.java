package org.example.hugmeexp.domain.shop.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hugmeexp.domain.mission.enums.FileUploadType;
import org.example.hugmeexp.domain.mission.util.FileUploadUtils;
import org.example.hugmeexp.domain.shop.dto.ProductRequest;
import org.example.hugmeexp.domain.shop.dto.ProductResponse;
import org.example.hugmeexp.domain.shop.entity.Product;
import org.example.hugmeexp.domain.shop.entity.ProductImage;
import org.example.hugmeexp.domain.shop.exception.ProductDeletedException;
import org.example.hugmeexp.domain.shop.exception.ProductNotFoundException;
import org.example.hugmeexp.domain.shop.mapper.ProductMapper;
import org.example.hugmeexp.domain.shop.repository.ProductImageRepository;
import org.example.hugmeexp.domain.shop.repository.ProductRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductAdminService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;
    private final FileUploadService fileUploadService;

    /**
     * 상품 등록 메서드
     * @param request
     * @return 생성된 Product의 Id
     * @throws IOException
     */
    @Transactional
    public Product registerProduct(ProductRequest request) {

        // Product 객체 생성
        Product product = Product.createProduct(
                request.getName(),
                request.getBrand(),
                request.getQuantity(),
                request.getPrice()
        );

        // 이미지 정보가 있는 경우 이미지 등록
        MultipartFile image = request.getImage();
        if (image != null && !image.isEmpty()) {
            //파일 처리 로직을 FileUploadService에 위임
            fileUploadService.uploadProductImage(product, image);
        }

        Product savedProduct = productRepository.save(product);
        log.info("Product saved with ID: {}", savedProduct.getId());
        return savedProduct;
    }

    /**
     * 상품 삭제 메서드
     * @param productId
     */
    @Transactional
    public void deleteProduct(Long productId) {

        // Id와 일치하는 상품이 없다면 예외 처리
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        // 해당 상품이 이미 삭제된 상품이면 예외 처리
        if (product.isDeleted()) {
            throw new ProductDeletedException();
        }

        log.info("Attempting to delete product ID: {}", productId);

        // 상품 삭제는 논리 삭제로 구현 (삭제된 상품이 과거 주문과 연관될 수 있음)
        product.delete();
        productRepository.save(product);

        log.info("Product successfully deleted.");
    }

    /**
     * 상품 수정 메서드
     * @param productId
     * @param request
     * @return
     */
    @Transactional
    public ProductResponse modifyProduct(Long productId, ProductRequest request) {

        // Id와 일치하는 상품이 없다면 예외 처리
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        // 해당 상품이 이미 삭제된 상품이면 예외 처리
        if (product.isDeleted()) {
            throw new ProductDeletedException();
        }

        // product 조회
        log.info("Modify product ID: {}", productId);

        // product 엔티티 수정 및 이미지 존재할 시 기존 이미지 삭제 후 생성
        product.updateProduct(request);

        MultipartFile image = request.getImage();
        if (image != null && !image.isEmpty()) {

            // product 엔티티에 이미지가 있었으면 기존 ProductImage 삭제 후 재생성
            if (product.isRegisterProductImage()) {
                log.info("Existing image for product {} will be replaced.", productId);
            }
            // 파일 처리 로직을 FileUploadService에 위임
            fileUploadService.uploadProductImage(product, image);
        }

        Product savedProduct = productRepository.save(product);
        log.info("Product saved with ID: {}", savedProduct.getId());
        return productMapper.toResponse(savedProduct);
    }

    // ===== 테스트용 =====
    public void increasePoint(String username) {
        User user = userRepository.findByUsername(username).get();
        user.increasePoint(100000);
        userRepository.save(user);
    }
}
