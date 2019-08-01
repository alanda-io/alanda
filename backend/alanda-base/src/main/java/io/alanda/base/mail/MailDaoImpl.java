/**
 *
 */
package io.alanda.base.mail;

import java.util.List;

import javax.persistence.EntityManager;

import io.alanda.base.dao.AbstractCrudDao;


/**
 * @author jlo
 */
public class MailDaoImpl extends AbstractCrudDao<Mail> implements MailDao {

  /**
   *
   */
  public MailDaoImpl() {
    // TODO Auto-generated constructor stub
  }

  /**
   * @param entityClass
   */
  public MailDaoImpl(Class<Mail> entityClass) {
    super(entityClass);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param em
   */
  public MailDaoImpl(EntityManager em) {
    super(em);

    // TODO Auto-generated constructor stub
  }

  @Override
  public List<Mail> getMailsByModuleName(String moduleName) {
    List<Mail> queryResults = em
          .createQuery("from Mail m where m.moduleName = :moduleName", Mail.class)
          .setParameter("moduleName", moduleName)
          .getResultList();
    return queryResults;
  }

  @Override
  public List<Mail> getMailsByModuleId(String moduleId) {
    List<Mail> queryResults = em
        .createQuery("from Mail m where m.moduleId = :moduleId", Mail.class)
        .setParameter("moduleId", moduleId)
        .getResultList();
    return queryResults;
  }

  @Override
  public List<Mail> getSortedMailsByModuleNameAndId(String moduleName, String moduleId) {
    List<Mail> queryResults = em
        .createQuery("from Mail m where m.moduleName = :moduleName and m.moduleId = :moduleId"
          + " order by createDate desc", Mail.class)
        .setParameter("moduleName", moduleName)
        .setParameter("moduleId", moduleId)
        .getResultList();
    return queryResults;
  }

}
