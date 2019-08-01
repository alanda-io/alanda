package io.alanda.base.dao;

import javax.persistence.EntityManager;

public interface CrudDao<T> extends Dao<T> {

	 T create(T t);
	 T update(T t);
	 void delete(T t); 
	 EntityManager getEntityManager();
}
