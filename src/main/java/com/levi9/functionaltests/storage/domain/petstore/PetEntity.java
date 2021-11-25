package com.levi9.functionaltests.storage.domain.petstore;

import com.levi9.functionaltests.rest.data.pet.CategoryDSO;
import com.levi9.functionaltests.rest.data.pet.PetStatus;
import com.levi9.functionaltests.rest.data.pet.TagDSO;

import java.util.List;

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
public class PetEntity {

	private Integer id;
	private CategoryDSO category;
	private String name;
	private List<String> photoUrls;
	private List<TagDSO> tags;
	private PetStatus status;
	@Default
	private boolean deleted = false;
}
