package org.example.hugmeexp.domain.shop.service;

import org.example.hugmeexp.domain.shop.dto.PurchaseRequest;
import org.example.hugmeexp.domain.shop.entity.Order;
import org.example.hugmeexp.domain.shop.entity.Product;
import org.example.hugmeexp.domain.shop.exception.NotEnoughPointException;
import org.example.hugmeexp.domain.shop.exception.OutOfQuantityException;
import org.example.hugmeexp.domain.shop.repository.OrderRepository;
import org.example.hugmeexp.domain.shop.repository.ProductRepository;
import org.example.hugmeexp.domain.user.entity.User;
import org.example.hugmeexp.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("상품 구매 성공")
    void purchase_success() {
        // given
        String purchaserUsername = "purchaser";
        PurchaseRequest request = new PurchaseRequest(1L, "receiver");

        User purchaser = User.builder().name("구매자").point(10000).build();
        User receiver = User.builder().phoneNumber("010-1234-5678").build();
        Product product = Product.builder().name("상품A").price(5000).quantity(10).build();

        when(userRepository.findByUsername("purchaser")).thenReturn(Optional.of(purchaser));
        when(userRepository.findByUsername("receiver")).thenReturn(Optional.of(receiver));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        productService.purchase(purchaserUsername, request);

        // then
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture()); // orderRepository.save가 1번 호출되었는지, 어떤 Order 객체가 저장되었는지 확인

        Order savedOrder = orderCaptor.getValue();
        assertEquals(purchaser, savedOrder.getUser()); // 저장된 주문의 구매자가 올바른지 확인
        assertEquals(product, savedOrder.getProduct()); // 저장된 주문의 상품이 올바른지 확인

        assertEquals(5000, purchaser.getPoint()); // 구매자 포인트가 정상적으로 차감되었는지 확인
        assertEquals(9, product.getQuantity()); // 상품 재고가 정상적으로 감소했는지 확인
    }

    @Test
    @DisplayName("상품 구매 실패 - 상품 재고 부족")
    void purchase_fail_outOfQuantity() {
        // given
        String purchaserUsername = "purchaser";
        PurchaseRequest request = new PurchaseRequest(1L, "receiver");
        User purchaser = User.builder().point(10000).build();
        Product product = Product.builder().price(5000).quantity(0).build(); // 재고 0

        when(userRepository.findByUsername(purchaserUsername)).thenReturn(Optional.of(purchaser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when & then
        assertThrows(OutOfQuantityException.class, () -> {
            productService.purchase(purchaserUsername, request);
        });
    }

    @Test
    @DisplayName("상품 구매 실패 - 포인트 부족")
    void purchase_fail_notEnoughPoint() {
        // given
        String purchaserUsername = "purchaser";
        PurchaseRequest request = new PurchaseRequest(1L, "receiver");
        User purchaser = User.builder().point(1000).build(); // 포인트 1000
        Product product = Product.builder().price(5000).quantity(10).build(); // 상품 가격 5000

        when(userRepository.findByUsername(purchaserUsername)).thenReturn(Optional.of(purchaser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when & then
        assertThrows(NotEnoughPointException.class, () -> {
            productService.purchase(purchaserUsername, request);
        });
    }
}