/**
 * 
 */
package io.alanda.base.batch;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * @author jlo
 */
public interface PmcBatchCreationService {

  String startBatchProjects(String projectType, InputStream inputStream);

  File getFileByPid(String pid);

  Map<String, Object> batchStartStatus(String pid);

}
