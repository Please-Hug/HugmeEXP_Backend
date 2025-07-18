package org.example.hugmeexp.domain.shop.service;

import org.example.hugmeexp.domain.shop.dto.ProductRequest;
import org.example.hugmeexp.domain.shop.entity.Product;
import org.example.hugmeexp.domain.shop.exception.ProductDeletedException;
import org.example.hugmeexp.domain.shop.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductAdminServiceTest {

    @InjectMocks
    private ProductAdminService productAdminService;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private FileUploadService fileUploadService;

    @Test
    @DisplayName("상품 등록 성공 - 이미지가 없는 경우")
    void registerProduct_withoutImage_success() {
        // given
        ProductRequest request = new ProductRequest(null, "test", "brand", 10, 100);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Product result = productAdminService.registerProduct(request);

        // then
        assertNotNull(result);
        assertEquals("test", result.getName());
        // fileUploadService가 호출되지 않았는지 검증
        verify(fileUploadService, never()).uploadProductImage(any(), any());
    }

    @Test
    @DisplayName("상품 삭제 성공")
    void deleteProduct_success() {
        // given
        Long productId = 1L;
        Product product = mock(Product.class); // Product 객체 자체를 mock으로 만듦
        when(product.isDeleted()).thenReturn(false); // 아직 삭제되지 않은 상태
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        productAdminService.deleteProduct(productId);

        // then
        verify(product, times(1)).delete(); // product 객체의 delete() 메서드가 1번 호출되었는지 검증
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("상품 삭제 실패 - 이미 삭제된 상품")
    void deleteProduct_fail_alreadyDeleted() {
        // given
        Long productId = 1L;
        Product deletedProduct = mock(Product.class);
        when(deletedProduct.isDeleted()).thenReturn(true); // 이미 삭제된 상태
        when(productRepository.findById(productId)).thenReturn(Optional.of(deletedProduct));

        // when & then
        assertThrows(ProductDeletedException.class, () -> {
            productAdminService.deleteProduct(productId);
        });
    }
}