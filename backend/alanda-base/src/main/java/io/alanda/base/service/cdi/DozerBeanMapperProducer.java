package io.alanda.base.service.cdi;

import javax.enterprise.inject.Produces;

import org.dozer.DozerBeanMapperSingletonWrapper;
import org.dozer.Mapper;

public class DozerBeanMapperProducer {

	@Produces
	@Dozer
	public Mapper getInstance() {
		 return DozerBeanMapperSingletonWrapper.getInstance();
	}
}
