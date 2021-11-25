package com.levi9.functionaltests.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Spring Configuration of functional test project where location of property files is defined.
 * Property Source can have multiple files defined from all of which properties can be read.
 *
 * @author Milos Pujic (m.pujic@levi9.com)
 */
@Configuration
@Scope("cucumber-glue")
@PropertySource("classpath:application-${env:development}.properties")
@ComponentScan({ "com.levi9.functionaltests" })
public class SpringConfig {

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

}
