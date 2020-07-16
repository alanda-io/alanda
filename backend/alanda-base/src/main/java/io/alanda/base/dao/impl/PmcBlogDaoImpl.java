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

  private static final Logger log = LoggerFactory.getLogger(PmcBlogDaoImpl.class);

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
    log.debug("Retrieving {} posts by status \"{}\", starting at {}...", count, status, first);
    List<PmcBlog> blogList;
    try {
      blogList = em
        .createQuery("select p from PmcBlog p where p.status = :status order by p.createDate desc", PmcBlog.class)
        .setParameter("status", status)
        .setFirstResult(first)
        .setMaxResults(count)
        .getResultList();
      log.trace("...found {} posts: {}", blogList.size(), blogList);
      return blogList;
    } catch (NoResultException e) {
      log.warn("Found no posts by status \"{}\", starting at {}", status, first);
      return null;
    }
  }

  @Override
  public PmcBlog getPostByBlogId(Long blog_id, String status) {
    log.debug("Retrieving post by id {} and status {}", blog_id, status);
    PmcBlog pmcBlog = (PmcBlog) em
      .createQuery("select p from PmcBlog p where p.blog_id = :blog_id and p.status = :status")
      .setParameter("blog_id", blog_id)
      .setParameter("status", status)
      .getSingleResult();
    return pmcBlog;
  }

  @Override
  public boolean atWork(Long oldBlogId) {
    log.debug("Retrieving count of posts with old id: {}", oldBlogId);
    //i = (int) em
    Long i = (Long) em
      .createQuery("select count(b) from PmcBlog b where b.predecessor_id = :oldBlogId")
      .setParameter("oldBlogId", oldBlogId)
      .getSingleResult();
    //    System.out.println(em
    //      .createQuery("select count(b) from PmcBlog b where b.predecessor_id = :oldBlogId")
    //      .setParameter("oldBlogId", oldBlogId)
    //      .getSingleResult());
    log.trace("...found {} posts with old id {}", i, oldBlogId);

    return i == 0;
  }

}
