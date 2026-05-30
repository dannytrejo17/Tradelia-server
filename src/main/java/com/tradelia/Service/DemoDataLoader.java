package com.tradelia.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradelia.Dto.DemoProductSeed;
import com.tradelia.Dto.DemoUserSeed;
import com.tradelia.Model.Product;
import com.tradelia.Model.User;
import com.tradelia.Repository.ProductRepository;
import com.tradelia.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class DemoDataLoader implements CommandLineRunner {

    private final DemoModeService demoModeService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    public DemoDataLoader(DemoModeService demoModeService,
                          UserRepository userRepository,
                          ProductRepository productRepository,
                          PasswordEncoder passwordEncoder,
                          ObjectMapper objectMapper) {
        this.demoModeService = demoModeService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!demoModeService.isDemoMode()) {
            return;
        }

        Map<String, User> usersByEmail = seedUsers();

        if (productRepository.count() > 0) {
            syncDemoCatalog(usersByEmail);
            return;
        }

        seedProducts(usersByEmail);
    }

    private Map<String, User> seedUsers() throws Exception {
        List<DemoUserSeed> seeds = readJson("data/demo-users.json", new TypeReference<>() {});
        Map<String, User> usersByEmail = new HashMap<>();

        for (DemoUserSeed seed : seeds) {
            User user = userRepository.findByEmail(seed.getEmail()).orElseGet(User::new);
            user.setUsername(seed.getUsername());
            user.setEmail(seed.getEmail());
            user.setPassword(passwordEncoder.encode(seed.getPassword()));
            user.setRole(seed.getRole() != null ? seed.getRole() : "USER");
            if (user.getCreated_at() == null) {
                user.setCreated_at(LocalDateTime.now());
            }
            usersByEmail.put(seed.getEmail(), userRepository.save(user));
        }

        return usersByEmail;
    }

    private void seedProducts(Map<String, User> usersByEmail) throws Exception {
        List<DemoProductSeed> seeds = readJson("data/demo-products.json", new TypeReference<>() {});

        for (DemoProductSeed seed : seeds) {
            User seller = usersByEmail.get(seed.getSellerEmail());
            if (seller == null) {
                continue;
            }

            Product product = new Product();
            applySeed(product, seed, seller);
            productRepository.save(product);
        }
    }

    private void syncDemoCatalog(Map<String, User> usersByEmail) throws Exception {
        List<DemoProductSeed> seeds = readJson("data/demo-products.json", new TypeReference<>() {});
        Set<String> seedNames = new HashSet<>();

        for (DemoProductSeed seed : seeds) {
            seedNames.add(seed.getName());
            User seller = usersByEmail.get(seed.getSellerEmail());
            if (seller == null) {
                continue;
            }

            Product product = productRepository.findByName(seed.getName()).orElseGet(Product::new);
            applySeed(product, seed, seller);
            productRepository.save(product);
        }

        for (Product product : productRepository.findAll()) {
            if (!seedNames.contains(product.getName())) {
                productRepository.delete(product);
            }
        }
    }

    private void applySeed(Product product, DemoProductSeed seed, User seller) {
        product.setName(seed.getName());
        product.setDescription(seed.getDescription());
        product.setPrice(seed.getPrice());
        product.setImage_url(seed.getImageUrl());
        product.setStock(seed.getStock());
        product.setCategory(seed.getCategory());
        product.setProduct_condition(seed.getProductCondition());
        product.setCity(seed.getCity());
        product.setProvince(seed.getProvince());
        product.setSeller(seller);
        if (product.getCreated_at() == null) {
            product.setCreated_at(LocalDateTime.now());
        }
    }

    private <T> T readJson(String path, TypeReference<T> type) throws Exception {
        ClassPathResource resource = new ClassPathResource(path);
        try (InputStream input = resource.getInputStream()) {
            return objectMapper.readValue(input, type);
        }
    }
}
