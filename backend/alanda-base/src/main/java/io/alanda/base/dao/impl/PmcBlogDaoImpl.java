/**
 * 
 */
package io.alanda.base.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dao.AbstractCrudDao;
import io.alanda.base.dao.PmcBlogDao;
import io.alanda.base.entity.PmcBlog;

/**
 * @author Jens Kornacker
 */
public class PmcBlogDaoImpl extends AbstractCrudDao<PmcBlog> implements PmcBlogDao {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * 
   */
  public PmcBlogDaoImpl() {
    // TODO Auto-generated constructor stub
  }

  /**
   * @param em
   */
  public PmcBlogDaoImpl(EntityManager em) {
    super(em);
    // TODO Auto-generated constructor stub
  }

  @Override
  public List<PmcBlog> getPostsByStatus(String status, int first, int count) {
    logger.info("status: " + status);
    List<PmcBlog> blogList;
    try {
      blogList = em
        .createQuery("select p from PmcBlog p where p.status = :status order by p.createDate desc", PmcBlog.class)
        .setParameter("status", status)
        .setFirstResult(first)
        .setMaxResults(count)
        .getResultList();
      logger.info("blogList: " + blogList.toString());
      return blogList;
    } catch (NoResultException e) {
      logger.info("NoResultException: " + e);
      return null;
    }

  }

  @Override
  public PmcBlog getPostByBlogId(Long blog_id, String status) {
    PmcBlog pmcBlog = new PmcBlog();
    pmcBlog = (PmcBlog) em
      .createQuery("select p from PmcBlog p where p.blog_id = :blog_id and p.status = :status")
      .setParameter("blog_id", blog_id)
      .setParameter("status", status)
      .getSingleResult();
    return pmcBlog;
  }

  @Override
  public boolean atWork(Long oldBlogId) {
    System.out.println("Dao: oldBlogId: ");
    System.out.println(oldBlogId);
    Long i;
    //i = (int) em
    i = (Long) em
      .createQuery("select count(b) from PmcBlog b where b.predecessor_id = :oldBlogId")
      .setParameter("oldBlogId", oldBlogId)
      .getSingleResult();
    //    System.out.println(em
    //      .createQuery("select count(b) from PmcBlog b where b.predecessor_id = :oldBlogId")
    //      .setParameter("oldBlogId", oldBlogId)
    //      .getSingleResult());
    logger.info("Long i: " + i.toString());
    if (i > 0) {
      return false;
    } else if (i == 0) {
      return true;
    }
    return false;
  }

}
