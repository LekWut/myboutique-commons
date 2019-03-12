package com.targa.labs.myboutique.common.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryDto {
	private Long id;
	private String name;
	private String description;
	private Set<ProductDto> products;
}