/**
 * 
 */
package io.alanda.base.batch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.batch.ExcelRoleConfig.RoleType;
import io.alanda.base.connector.PmcProjectBatchCreateDelegate;
import io.alanda.base.dto.PmcProjectTypeDto;
import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.dto.RefObject;
import io.alanda.base.service.ConfigService;
import io.alanda.base.service.PmcProjectService;
import io.alanda.base.service.PmcPropertyService;
import io.alanda.base.service.PmcUserService;
import io.alanda.base.type.ProcessVariables;
import io.alanda.base.util.UserContext;

/**
 * @author jlo
 */
@Singleton
@ApplicationScoped
public class PmcBatchCreationServiceImpl implements PmcBatchCreationService {

  private static final Logger log = LoggerFactory.getLogger(PmcBatchCreationServiceImpl.class);

  @Inject
  private Instance<PmcProjectBatchCreateDelegate> batchCreateDelegate;

  @Inject
  private PmcProjectService projectService;

  @Inject
  private ConfigService configService;

  @Inject
  private RuntimeService runtimeService;

  @Inject
  private PmcUserService userService;

  /**
   * 
   */
  public PmcBatchCreationServiceImpl() {

  }

  @Override
  public String startBatchProjects(String projectTypeName, InputStream inputStream) {
    log.info("Starting batch creation for project type {}", projectTypeName);

    PmcProjectTypeDto projectType = this.projectService.getProjectType(projectTypeName);
    try {
      return parseExcel(projectType, inputStream);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private String parseExcel(PmcProjectTypeDto projectType, InputStream input) throws Exception {
    log.info("Parsing excel for project type {}...", projectType);

    Workbook wb = WorkbookFactory.create(input);

    int n = wb.getNumberOfSheets();
    if (n == 0)
      throw new RuntimeException("Excel file has no sheets!");

    Sheet sheet = wb.getSheetAt(0);

    Row configRow = sheet.getRow(1);

    ExcelProjectConfig cfg = parseConfig(configRow);
    log.info("...parsed Excel File, config: {}", cfg);
    final String successMessage = "Check OK, starting process...";

    List<BatchProjectDto> batchProjects = new ArrayList<>();
    Map<String, PmcUserDto> cache = new HashMap<>();
    for (Row r : sheet) {
      if (r.getRowNum() > 2 &&
        !(cfg.getResultMessage() != null &&
          r.getCell(cfg.getResultMessage()) != null &&
          r.getCell(cfg.getResultMessage()).getStringCellValue().equals(successMessage))) {

        try {

          BatchProjectDto batchProject = new BatchProjectDto();

          batchProject.setRowNumber(r.getRowNum());

          //read Project data
          Cell c = r.getCell(cfg.getTitle());
          if (c == null)
            throw new Exception("Provide project Title!");
          if (StringUtils.isBlank(c.getStringCellValue()))
            continue;
          batchProject.setTitle(c.getStringCellValue());

          if (cfg.getSubType() != null) {
            c = r.getCell(cfg.getSubType());
            if (c == null)
              throw new Exception("Provide project SubType!");
            batchProject.setSubType(c.getStringCellValue());
            if ( !projectType.getAllowedSubtypeList().contains(batchProject.getSubType())) {
              throw new Exception("Subtype " + batchProject.getSubType() + " not valid!");
            }
          }
          if (cfg.getTag() != null) {
            c = r.getCell(cfg.getTag());
            if (c == null) {
              throw new Exception("Provide project Tag(s)!");
            }
            batchProject.setTag(c.getStringCellValue());
            if ( !StringUtils.isEmpty(batchProject.getTag()) && !projectType.getAllowedTagList().contains(batchProject.getTag())) {
              throw new Exception("Tag " + batchProject.getTag() + " not valid!");
            }
          }
          c = r.getCell(cfg.getComment());
          if (c == null)
            throw new Exception("Provide project Comments!");
          batchProject.setComment(c.getStringCellValue());

          c = r.getCell(cfg.getRefObjectIdName());
          if (c == null)
            throw new Exception("Provide object idName!");
          String refObjectIdName = c.getStringCellValue();
          if (StringUtils.isBlank(refObjectIdName)) {
            throw new Exception("Provide object idName!");
          }
          refObjectIdName = refObjectIdName.trim();
          RefObject ref = this.projectService.getRefObjectLoader(projectType.getObjectType()).getRefObjectByName(refObjectIdName);
          if (ref == null)
            throw new Exception("No object with idName " + refObjectIdName + " found!");

          batchProject.setRefObjectIdName(refObjectIdName);
          batchProject.setRefObjectId(ref.getRefObjectId());

          c = r.getCell(cfg.getPrio());
          if (c == null)
            throw new Exception("Provide priority!");
          try {
            batchProject.setPrio((int) c.getNumericCellValue());
          } catch (Exception e) {
            throw new Exception("Could not read priority, please provide an integer number!");
          }

          c = r.getCell(cfg.getDueDate());
          if (c == null)
            throw new Exception("Provide due date!");
          try {
            batchProject.setDueDate(c.getDateCellValue());
          } catch (Exception e) {
            throw new Exception("Could not parse due date!");
          }

          for (ExcelRoleConfig subConfig : cfg.getRoleMap().values()) {
            c = r.getCell(subConfig.getCol());
            if (c == null)
              continue;
            String roleValue = c.getStringCellValue();
            if (StringUtils.isEmpty(roleValue))
              continue;
            BatchProjectRoleDto role = processRole(roleValue, subConfig, cache);
            batchProject.getRoles().add(role);
          }
          for (ExcelPropertyConfig subConfig : cfg.getPropertyMap().values()) {
            c = r.getCell(subConfig.getCol());
            if (c == null)
              continue;
            String propValue;
            if (Objects.equals(subConfig.getType(), PmcPropertyService.PmcPropertyType.DATE)) {
              SimpleDateFormat sdf = new SimpleDateFormat(PmcPropertyService.dateFormat);
              Date propDateValue = c.getDateCellValue();
              propValue = propDateValue != null ? sdf.format(propDateValue) : null;
            } else {
              propValue = c.getStringCellValue();
            }
            if (StringUtils.isEmpty(propValue))
              continue;
            BatchProjectPropertyDto prop = new BatchProjectPropertyDto(subConfig.getName(), subConfig.getType(), propValue);
            batchProject.getProperties().add(prop);
          }
          for (ExcelProcessConfig subConfig : cfg.getProcessMap().values()) {
            c = r.getCell(subConfig.getColStart());
            if (c == null)
              continue;
            String sStart = c.getStringCellValue();
            if (StringUtils.isEmpty(sStart) || !sStart.equalsIgnoreCase("yes"))
              continue;
            String comment = null;
            c = r.getCell(subConfig.getColComment());
            if (c != null) {
              comment = c.getStringCellValue();
            }
            if (comment == null)
              comment = "";
            batchProject.getProcesses().add(new BatchProjectProcessDto(subConfig.getName(), comment));
          }

          batchProjects.add(batchProject);

          c = r.getCell(cfg.getResultMessage());
          if (c == null)
            c = r.createCell(cfg.getResultMessage());
          c.setCellValue(successMessage);

        } catch (RuntimeException e) {
          log.error(e.getMessage());
          throw new RuntimeException(e);
        } catch (Exception e) {
          log.error(e.getMessage(), e);
          Cell c = r.getCell(cfg.getResultMessage());
          if (c == null)
            c = r.createCell(cfg.getResultMessage());
          c.setCellValue(e.getMessage());
        }
      }
    }
    log.info("Parsed Data: {}", batchProjects);
    Map<String, Object> vars = new HashMap<>();
    vars.put(BatchVariables.PROJECT_TYPE, projectType.getIdName());
    vars.put(BatchVariables.PROJECT_CONFIG, cfg);
    vars.put(BatchVariables.PROJECT_LIST, batchProjects);
    vars.put(BatchVariables.PROJECT_COUNTER, 0);
    vars.put(BatchVariables.PROJECT_TOTAL, batchProjects.size());
    vars.put(ProcessVariables.CREATION_USER, UserContext.getUser().getGuid());
    vars.put(BatchVariables.PROJECT_DONE, false);

    ProcessInstance pi = runtimeService.startProcessInstanceByKey("batch-create-projects", vars);

    FileOutputStream fileOut = new FileOutputStream(getFileByPid(pi.getProcessInstanceId()));
    wb.write(fileOut);
    fileOut.flush();
    fileOut.close();

    return pi.getProcessInstanceId();

  }

  private BatchProjectRoleDto processRole(String roleValue, ExcelRoleConfig subConfig, Map<String, PmcUserDto> cache) throws Exception {

    PmcUserDto user = null;

    if (cache.get(roleValue) != null) {
      user = cache.get(roleValue);
    } else {
      if (subConfig.getType() == RoleType.FULLNAME) {
        List<PmcUserDto> l = userService.getUsersByGroup(null, roleValue.toLowerCase());
        if ( !l.isEmpty()) {
          user = l.get(0);

        }

      } else if (subConfig.getType() == RoleType.USERNAME) {
        user = userService.getUserByLoginName(roleValue);
      } else if (subConfig.getType() == RoleType.CUSTOM) {
        try {
          for (PmcProjectBatchCreateDelegate d : batchCreateDelegate) {
            if (d.isUserTransform()) {
              user = d.tranformUser(roleValue);
              break;
            }
          }

        } catch (Exception ex) {
          throw new Exception("User " + roleValue + " no valid userId for group " + subConfig.getName());
        }
      } else if (subConfig.getType() == RoleType.GUID) {
        try {
          user = userService.getUserByUserId(Long.valueOf(roleValue));
        } catch (Exception ex) {
          throw new Exception("User " + roleValue + " no valid userId for group " + subConfig.getName());
        }
      }
      if (user == null) {
        throw new Exception("User " + roleValue + " not found for group " + subConfig.getName());
      }
      cache.put(roleValue, user);
    }
    BatchProjectRoleDto role = new BatchProjectRoleDto();
    role.setName(subConfig.getName());
    role.setValue(user.getGuid().toString());
    return role;

  }

  public File getFileByPid(String pid) {
    File file = new File(
      configService.getProperty(ConfigService.DOCUMENT_ROOT_DIR) +
        "/documents/os_doc_camunda/os_doc_batch/create-batch-project-" +
        pid +
        ".xlsx");
    file.getParentFile().mkdirs();
    return file;
  }

  private ExcelProjectConfig parseConfig(Row configRow) {
    ExcelProjectConfig config = new ExcelProjectConfig();
    for (Cell c : configRow) {
      String val = c.getStringCellValue();
      if ( !StringUtils.isEmpty(val)) {
        config.add(c.getColumnIndex(), val);
      }
    }

    return config;
  }

  @Override
  public Map<String, Object> batchStartStatus(String pid) {
    Map<String, Object> result = new HashMap<>();

    ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(pid).singleResult();

    if (pi == null)
      result.put("finished", true);
    else {
      result.put("finished", false);
      result.put("counter", runtimeService.getVariable(pid, BatchVariables.PROJECT_COUNTER));
      result.put("total", runtimeService.getVariable(pid, BatchVariables.PROJECT_TOTAL));
    }

    return result;
  }

}
