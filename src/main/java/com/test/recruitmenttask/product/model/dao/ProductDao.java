package com.test.recruitmenttask.product.model.dao;


import com.test.recruitmenttask.product.model.dto.ProductDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductDao {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  Integer id;
  @Column
  String name;
  @Column
  BigDecimal price;

  public ProductDao(String name, BigDecimal price) {
    this.name = name;
    this.price = price;
  }

  public ProductDto mapToProductDto() {
    return new ProductDto(this.getId(), this.getName(), this.getPrice());
  }
}