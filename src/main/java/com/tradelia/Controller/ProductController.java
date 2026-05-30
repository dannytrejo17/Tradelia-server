package com.tradelia.Controller;

import com.tradelia.Dto.FilterRequest;
import com.tradelia.Dto.ProductDto;
import com.tradelia.Service.DemoModeService;
import com.tradelia.Service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final DemoModeService demoModeService;

    public ProductController(ProductService productService, DemoModeService demoModeService) {
        this.productService = productService;
        this.demoModeService = demoModeService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getProducts() {
        return ResponseEntity.ok(productService.getAllRandom());
    }

    @GetMapping("/mios")
    public ResponseEntity<List<ProductDto>> getMyProducts(Principal principal) {
        return ResponseEntity.ok(productService.getMine(principal));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PostMapping("/filter")
    public ResponseEntity<List<ProductDto>> filter(@RequestBody FilterRequest request) {
        return ResponseEntity.ok(productService.filter(request.getProvince(), request.getCity()));
    }

    @PostMapping(value = "/sell", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> sellProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("stock") int stock,
            @RequestParam("category") String category,
            @RequestParam("condition") String condition,
            @RequestParam("city") String city,
            @RequestParam("province") String province,
            @RequestParam("image") MultipartFile image,
            Principal principal) throws IOException {
        demoModeService.ensureWriteAllowed();
        String status = productService.create(
                principal, name, description, price, stock,
                category, condition, city, province, image);
        return new ResponseEntity<>(status, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Long id,
                                                @RequestBody ProductDto req,
                                                Principal principal) {
        demoModeService.ensureWriteAllowed();
        String status = productService.update(principal, id, req);
        return ResponseEntity.ok(status);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id, Principal principal) {
        demoModeService.ensureWriteAllowed();
        String status = productService.delete(principal, id);
        return ResponseEntity.ok(status);
    }
}
