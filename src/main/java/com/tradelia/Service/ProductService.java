package com.tradelia.Service;

import com.tradelia.Dto.ProductDto;
import com.tradelia.Model.Product;
import com.tradelia.Model.User;
import com.tradelia.Repository.ProductRepository;
import com.tradelia.Repository.UserRepository;
import com.tradelia.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    public ProductService(ProductRepository productRepository,
                          UserRepository userRepository,
                          CloudinaryService cloudinaryService) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public List<ProductDto> getAllRandom() {
        List<Product> products = productRepository.findAll();
        Collections.shuffle(products);
        List<ProductDto> result = new ArrayList<>();
        for (Product product : products) {
            result.add(ProductDto.from(product));
        }
        return result;
    }

    public ProductDto getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "producto no encontrado"));
        return ProductDto.from(product);
    }

    public List<ProductDto> getMine(Principal principal) {
        User seller = loadUser(principal);
        List<ProductDto> result = new ArrayList<>();
        for (Product product : productRepository.findBySeller(seller)) {
            result.add(ProductDto.from(product));
        }
        return result;
    }

    public String create(Principal principal,
                         String name,
                         String description,
                         BigDecimal price,
                         int stock,
                         String category,
                         String condition,
                         String city,
                         String province,
                         MultipartFile image) throws IOException {
        User seller = loadUser(principal);

        Product product = new Product();
        product.setSeller(seller);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategory(category);
        product.setProduct_condition(condition);
        product.setCity(city);
        product.setProvince(province);
        product.setImage_url(cloudinaryService.uploadImage(image));
        product.setCreated_at(LocalDateTime.now());

        productRepository.save(product);
        return "producto creado";
    }

    public String update(Principal principal, Long id, ProductDto req) {
        User seller = loadUser(principal);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "producto no encontrado"));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "no tienes permiso");
        }

        if (req.getName() != null) product.setName(req.getName());
        if (req.getDescription() != null) product.setDescription(req.getDescription());
        if (req.getPrice() != null) product.setPrice(req.getPrice());
        if (req.getCity() != null) product.setCity(req.getCity());
        if (req.getProvince() != null) product.setProvince(req.getProvince());
        if (req.getCategory() != null) product.setCategory(req.getCategory());

        productRepository.save(product);
        return "producto modificado";
    }

    public String delete(Principal principal, Long id) {
        User seller = loadUser(principal);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "producto no encontrado"));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "no tienes permiso");
        }

        productRepository.delete(product);
        return "producto eliminado";
    }

    public List<ProductDto> filter(String province, String city) {
        if ((province == null || province.isBlank()) && (city == null || city.isBlank())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Debe ingresar al menos una provincia o ciudad");
        }

        List<Product> products;
        if (province != null && !province.isBlank() && city != null && !city.isBlank()) {
            products = productRepository.findByProvinceAndCity(province, city);
        } else if (province != null && !province.isBlank()) {
            products = productRepository.findByProvince(province);
        } else {
            products = productRepository.findByCity(city);
        }

        List<ProductDto> result = new ArrayList<>();
        for (Product product : products) {
            result.add(ProductDto.from(product));
        }
        return result;
    }

    private User loadUser(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "no autenticado"));
    }
}
