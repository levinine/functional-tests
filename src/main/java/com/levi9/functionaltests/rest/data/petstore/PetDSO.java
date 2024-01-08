package com.levi9.functionaltests.rest.data.petstore;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class PetDSO {

	private Integer id;
	private CategoryDSO category;
	private String name;
	private List<String> photoUrls;
	private List<TagDSO> tags;
	private String status;
}
