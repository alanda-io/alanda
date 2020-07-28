/**
 *
 */
package io.alanda.base.mail;

import java.util.List;

import javax.persistence.EntityManager;

import io.alanda.base.dao.AbstractCrudDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author jlo
 */
public class MailDaoImpl extends AbstractCrudDao<Mail> implements MailDao {
  private static final Logger log = LoggerFactory.getLogger(MailDaoImpl.class);

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
    log.debug("Retrieving mails by module name {}", moduleName);

    List<Mail> queryResults = em
          .createQuery("from Mail m where m.moduleName = :moduleName", Mail.class)
          .setParameter("moduleName", moduleName)
          .getResultList();
    return queryResults;
  }

  @Override
  public List<Mail> getMailsByModuleId(String moduleId) {
    log.debug("Retrieving mails by module id {}", moduleId);

    List<Mail> queryResults = em
        .createQuery("from Mail m where m.moduleId = :moduleId", Mail.class)
        .setParameter("moduleId", moduleId)
        .getResultList();
    return queryResults;
  }

  @Override
  public List<Mail> getSortedMailsByModuleNameAndId(String moduleName, String moduleId) {
    log.debug("Retrieving sorted mails by module with name {} and id {}", moduleName, moduleId);

    List<Mail> queryResults = em
        .createQuery("from Mail m where m.moduleName = :moduleName and m.moduleId = :moduleId"
          + " order by createDate desc", Mail.class)
        .setParameter("moduleName", moduleName)
        .setParameter("moduleId", moduleId)
        .getResultList();
    return queryResults;
  }

}
