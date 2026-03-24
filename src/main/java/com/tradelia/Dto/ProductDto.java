package com.tradelia.Dto;

import com.tradelia.Model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String image_url;
    private String city;
    private String province;
    private String category;

    public static ProductDto from(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImage_url(),
                product.getCity(),
                product.getProvince(),
                product.getCategory()
        );
    }
}
