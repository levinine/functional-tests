package com.levi9.functionaltests.rest.data.randomimage;

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
public class RandomDogImageDSO {

	private int fileSizeBytes;
	private String url;
}
