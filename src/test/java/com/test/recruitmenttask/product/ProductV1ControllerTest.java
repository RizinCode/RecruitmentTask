package com.test.recruitmenttask.product;

import com.test.recruitmenttask.RecruitmentTaskApplication;
import com.test.recruitmenttask.common.helper.ErrorType;
import com.test.recruitmenttask.common.model.ApiErrorResponse;
import com.test.recruitmenttask.product.model.dao.ProductDao;
import com.test.recruitmenttask.product.model.dto.ProductDto;
import com.test.recruitmenttask.product.model.dto.ProductsListDto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@SpringBootTest(classes = RecruitmentTaskApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductV1ControllerTest {

  @LocalServerPort
  private Integer serverPort;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private ProductRepository productRepository;

  @Container
  static MySQLContainer<?> container = new MySQLContainer("mysql")
      .withDatabaseName("recruitment_task")
      .withUsername("rt_user")
      .withPassword("secret");

  @DynamicPropertySource
  static void setup(DynamicPropertyRegistry registry) {
    container.start();
    registry.add("spring.datasource.url", container::getJdbcUrl);
    registry.add("spring.datasource.username", container::getUsername);
    registry.add("spring.datasource.password", container::getPassword);
  }

  @AfterEach
  void clear() {
    productRepository.deleteAll();
  }

  @Test
  void getProductsList_shouldReturnEmptyList_whenNoProductsWereAdded() throws Exception {
    var response = getProductsList(ProductsListDto.class, "0", "20");

    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
    Assertions.assertEquals(0, response.getBody().pageNumber());
    Assertions.assertEquals(20, response.getBody().pageSize());
    Assertions.assertTrue(response.getBody().products().isEmpty());
  }

  @Test
  void getProductsList_shouldReturnMax10Products_whenNoHeaderAdded() throws Exception {
    var products = generateProductList(30);
    productRepository.saveAll(products);

    var response = getProductsList(ProductsListDto.class, null, null);

    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
    Assertions.assertEquals(0, response.getBody().pageNumber());
    Assertions.assertEquals(10, response.getBody().pageSize());
    Assertions.assertEquals(10, response.getBody().products().size());
  }

  @Test
  void getProductsList_shouldReturn100Products() throws Exception {
    var products = generateProductList(120);
    productRepository.saveAll(products);

    var response = getProductsList(ProductsListDto.class, "0", "100");

    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
    Assertions.assertEquals(0, response.getBody().pageNumber());
    Assertions.assertEquals(100, response.getBody().pageSize());
    Assertions.assertEquals(100, response.getBody().products().size());
  }

  @Test
  void getProductsList_shouldReturn10ProductsOnAnotherPage() throws Exception {
    var products = generateProductList(30);
    productRepository.saveAll(products);

    var response = getProductsList(ProductsListDto.class, "2", "10");

    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
    Assertions.assertEquals(2, response.getBody().pageNumber());
    Assertions.assertEquals(10, response.getBody().pageSize());
    Assertions.assertEquals(10, response.getBody().products().size());
  }

  @Test
  void getProduct_shouldReturnGivenProduct() throws Exception {
    var product = generateProductDao();
    var id = productRepository.save(product).getId();

    var response = getProduct(ProductDto.class, id);

    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
    Assertions.assertTrue(product.getName().equals(response.getBody().name()));
  }

  @Test
  void getProduct_shouldReturn404NotFound_whenProductDoesNotExist() throws Exception {
    Random random = new Random();
    var randomId = random.nextInt();
    var response = getProduct(ApiErrorResponse.class, randomId);

    Assertions.assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404)));
    Assertions.assertEquals("productNotFound", response.getBody().error());
    Assertions.assertEquals("Product not found", response.getBody().description());
  }

  @Test
  void createProduct_shouldReturn200() throws Exception {
    var product = new ProductDto(999, "Test", BigDecimal.valueOf(12.34));

    var response = createProduct(ProductDto.class, product);

    Awaitility.with()
            .pollInterval(1, TimeUnit.SECONDS)
            .atMost(3, TimeUnit.SECONDS)
            .await()
            .until(() -> productRepository.findById(response.getBody().id()).isPresent());

    Optional<ProductDao> daoProduct = productRepository.findById(response.getBody().id());

    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
    Assertions.assertTrue(daoProduct.isPresent());
    Assertions.assertTrue(daoProduct.get().getName().equals(product.name()));
    Assertions.assertTrue(daoProduct.get().getPrice().equals(product.price()));
  }

  @Test
  void createProduct_shouldReturn409Conflict_whenProductAlreadyExists() throws Exception {
    var product = new ProductDto(999, "Test", BigDecimal.valueOf(12.34));
    productRepository.save(new ProductDao(product.name(), product.price()));

    var response = createProduct(ApiErrorResponse.class, product);

    Assertions.assertTrue(response.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(409)));
    Assertions.assertEquals("productNameAlreadyExists", response.getBody().error());
    Assertions.assertEquals("Product name already exists", response.getBody().description());
  }

  @Test
  void deleteProduct_shouldReturn200AndDeleteProduct() throws Exception {
    var product = new ProductDto(999, "Test", BigDecimal.valueOf(12.34));
    var productId = productRepository.save(new ProductDao(product.name(), product.price())).getId();

    var response = deleteProduct(Void.class, productId);

    Awaitility.with()
        .pollInterval(1, TimeUnit.SECONDS)
        .atMost(3, TimeUnit.SECONDS)
        .await()
        .until(() -> productRepository.findById(productId).isEmpty());

    Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
    Assertions.assertTrue(productRepository.findById(productId).isEmpty());
  }

  private <T> ResponseEntity<T> getProductsList(Class<T> responseType, String pageNumber, String pageSize) {
    var headers = new HttpHeaders();
    if (pageNumber != null && pageSize != null) {
      headers.set("pageNumber", pageNumber);
      headers.set("pageSize", pageSize);
    }
    return restTemplate.exchange("http://localhost:" + serverPort + "/app/v1/products", HttpMethod.GET, new HttpEntity<Void>(headers), responseType);
  }

  private <T> ResponseEntity<T> getProduct(Class<T> responseType, Integer productId) {
    return restTemplate.exchange("http://localhost:" + serverPort + "/app/v1/products/" + productId, HttpMethod.GET, new HttpEntity<Void>(new HttpHeaders()), responseType);
  }

  private <T> ResponseEntity<T> createProduct(Class<T> responseType, ProductDto productDto) {
    return restTemplate.exchange("http://localhost:" + serverPort + "/app/v1/products", HttpMethod.POST, new HttpEntity<>(productDto), responseType);
  }

  private <T> ResponseEntity<T> deleteProduct(Class<T> responseType, Integer productId) {
    return restTemplate.exchange("http://localhost:" + serverPort + "/app/v1/products/" + productId, HttpMethod.DELETE, new HttpEntity<Void>(new HttpHeaders()), responseType);
  }

  private List<ProductDao> generateProductList(int number) {
    List<ProductDao> mockProducts = new ArrayList<>();
    for (int i = 0; i < number; i++) {
      mockProducts.add(generateProductDao());
    }
    return mockProducts;
  }

  private ProductDao generateProductDao() {
    var name = RandomStringUtils.randomAlphabetic(10);
    var price = new BigDecimal(Math.random());
    return new ProductDao(name, price);
  }
}