/**
 *
 */
package io.alanda.base.mail;

import java.util.List;

import io.alanda.base.dao.CrudDao;


/**
 * @author jlo
 */
public interface MailDao extends CrudDao<Mail> {

  List<Mail> getMailsByModuleName(String moduleName);
  List<Mail> getMailsByModuleId(String moduleId);
  List<Mail> getSortedMailsByModuleNameAndId(String moduleName, String moduleId);

}
