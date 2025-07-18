package org.example.hugmeexp.domain.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class ProductResponse {

    private final Long id;
    private final String name;
    private final String brand;
    private final int quantity;
    private final int price;
    private final String imageUrl;

    // 로그인 사용자가 구매 가능한 상품인지
    private final boolean available;
}
