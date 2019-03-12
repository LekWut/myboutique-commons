package com.targa.labs.myboutique.common.dto;

import java.math.BigDecimal;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
	private Long id;
	private String name;
	private String description;
	private BigDecimal price;
	private Integer quantity;
	private String status;
	private Integer salesCounter;
	private Set<ReviewDto> reviews;
	private CategoryDto category;
}
