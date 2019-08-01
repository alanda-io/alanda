package io.alanda.base.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class JPABasedTest {

  protected static EntityManagerFactory emf;

  protected EntityManager em;

  @BeforeClass
  public static void createEntityManagerFactory() {
    emf = Persistence.createEntityManagerFactory("pmcDB");
  }

  @AfterClass
  public static void closeEntityManagerFactory() {
    emf.close();
  }

  @Before
  public void beginTransaction() {
    em = emf.createEntityManager();
    em.getTransaction().begin();
  }

  @After
  public void rollbackTransaction() {
    if (em.getTransaction().isActive()) {
      em.getTransaction().rollback();
    }

    if (em.isOpen()) {
      em.close();
    }
  }

}
