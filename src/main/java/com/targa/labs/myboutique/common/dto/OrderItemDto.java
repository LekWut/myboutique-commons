package com.targa.labs.myboutique.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class OrderItemDto {
	private Long id;
	private Long quantity;
	private Long productId;
	private Long orderId;
}
