package com.levi9.functionaltests.storage.domain.automationpractice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Nikola Komazec (n.komazec@levi9.com)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DressEntity {

	private Integer quantity;
	private String size;
	private String color;
	private String type;
	private Double price;

}
