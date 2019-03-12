package com.targa.labs.myboutique.common.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderDto {
	private Long id;
	private BigDecimal totalPrice;
	private String status;
	private ZonedDateTime shipped;
	private PaymentDto payment;
	private AddressDto shipmentAddress;
	private Set<OrderItemDto> orderItems;
}