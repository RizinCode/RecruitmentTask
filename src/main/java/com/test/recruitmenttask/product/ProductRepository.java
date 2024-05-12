package com.test.recruitmenttask.product;

import com.test.recruitmenttask.product.model.dao.ProductDao;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ProductRepository extends CrudRepository<ProductDao, Integer> {

  List<ProductDao> findAll(Pageable pageable);
  Optional<ProductDao> findByName(String name);

}
