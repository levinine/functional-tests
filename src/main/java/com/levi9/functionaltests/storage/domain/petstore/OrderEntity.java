package com.levi9.functionaltests.storage.domain.petstore;

import com.levi9.functionaltests.rest.data.store.OrderStatus;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderEntity {

	private Integer id;
	private PetEntity pet;
	private Integer quantity;
	private LocalDateTime shipDate;
	private OrderStatus status;
	private Boolean complete;
	@Default
	private boolean deleted = false;
}
