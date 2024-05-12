package com.test.recruitmenttask.product;

import com.test.recruitmenttask.product.model.dao.ProductDao;
import com.test.recruitmenttask.product.model.dto.ProductDto;
import com.test.recruitmenttask.product.model.dto.ProductsListDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/v1/products")
public class ProductV1Controller {
  private final ProductService productService;

  @Autowired
  public ProductV1Controller(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProductsListDto> getProductsList(@RequestHeader(required = false, defaultValue = "0") Integer pageNumber,
                                                         @RequestHeader(required = false, defaultValue = "10") Integer pageSize) {
    return ResponseEntity.ok(productService.getProductsList(pageNumber, pageSize));
  }

  @GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProductDto> getProduct(@PathVariable Integer productId) {
    return ResponseEntity.ok(productService.getProduct(productId));
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProductDao> createProduct(@Valid @RequestBody ProductDto productDto) {
    return ResponseEntity.ok(productService.createProduct(productDto));
  }

  @DeleteMapping(value = "/{productId}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Integer productId) {
    productService.deleteProduct(productId);
    return ResponseEntity.accepted().build();
  }
}
