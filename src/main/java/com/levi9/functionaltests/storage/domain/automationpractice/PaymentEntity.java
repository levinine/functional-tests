package com.levi9.functionaltests.storage.domain.automationpractice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Djordje Borisavljevic (dj.borisavljevic@levi9.com)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PaymentEntity {

	private String paymentMethod;
}
