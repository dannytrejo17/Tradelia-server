package com.tradelia.Repository;

import com.tradelia.Model.Product;
import com.tradelia.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findBySeller(User seller);

    List<Product> findByProvince(String province);

    List<Product> findByCity(String city);

    List<Product> findByProvinceAndCity(String province, String city);

    Optional<Product> findByName(String name);
}
