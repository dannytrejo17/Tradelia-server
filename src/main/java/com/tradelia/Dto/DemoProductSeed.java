package com.tradelia.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemoProductSeed {

    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Integer stock;
    private String category;
    private String productCondition;
    private String city;
    private String province;
    private String sellerEmail;
}
