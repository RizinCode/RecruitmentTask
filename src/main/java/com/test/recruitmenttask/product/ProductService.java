package com.test.recruitmenttask.product;

import com.test.recruitmenttask.common.exception.ReadableException;
import com.test.recruitmenttask.common.helper.ErrorType;
import com.test.recruitmenttask.product.model.dao.ProductDao;
import com.test.recruitmenttask.product.model.dto.ProductDto;
import com.test.recruitmenttask.product.model.dto.ProductsListDto;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

  private final ProductRepository productRepository;

  @Autowired
  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public ProductsListDto getProductsList(Integer pageNumber, Integer pageSize) {
    return new ProductsListDto(pageNumber, pageSize, getProducts(pageNumber, pageSize));
  }

  public List<ProductDto> getProducts(Integer pageNumber, Integer pageSize) {
    List<ProductDto> productDtoList = new ArrayList<>();
    productRepository.findAll(PageRequest.of(pageNumber, pageSize)).forEach(productDao -> productDtoList.add(productDao.mapToProductDto()));
    return productDtoList;
  }

  public ProductDto getProduct(Integer productId) {
    ProductDao productDao = productRepository.findById(productId).orElseThrow(
        () -> new ReadableException(ErrorType.PRODUCT_NOT_FOUND));
    return productDao.mapToProductDto();
  }

  @Transactional
  public ProductDao createProduct(ProductDto productDto) {
    ProductDao productDao;
    if (productRepository.findByName(productDto.name()).isEmpty()) {
      productDao = new ProductDao(productDto.name(), productDto.price());
    } else {
      throw new ReadableException(ErrorType.PRODUCT_NAME_ALREADY_EXISTS);
    }
    return productRepository.save(productDao);
  }

  @Transactional
  public void deleteProduct(Integer productId) {
    productRepository.deleteById(productId);
  }

}
