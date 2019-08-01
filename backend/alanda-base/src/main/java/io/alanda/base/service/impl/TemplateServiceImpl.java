package io.alanda.base.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.tools.generic.DateTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.service.TemplateService;
import io.alanda.base.service.cdi.JBossHome;

@Singleton
@ApplicationScoped()
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class TemplateServiceImpl implements TemplateService {

  private static final String VELOCITY_PROPERTIES_LOCATION = "/velocity/velocity.properties";

  private static final String VELOCITY_TEMPLATE_LOCATION_KEY = "file.resource.loader.path";

  private final Logger logger = LoggerFactory.getLogger(TemplateServiceImpl.class);

  /**
   * Location where the template folder should be located. (e.g. Home directory of JBoss or Test Resources)
   */
  @Inject
  @JBossHome
  private String rootLocation;

  public TemplateServiceImpl() {

  }

  public TemplateServiceImpl(String rootLocation) {
    rootLocation = removeProtocolFromPath(rootLocation);
    rootLocation = FilenameUtils.separatorsToSystem(rootLocation);
    this.rootLocation = rootLocation;
  }

  @Override
  public String getTemplateString(String templateName, String key, Object value) {
    VelocityContext context = new VelocityContext();
    context.put(key, value);
    context.put("date", new DateTool());
    return getTemplateString(templateName, context);

  }

  @Override
  public String getTemplateString(String templateName, Map<String, Object> templateValues) {
    VelocityContext context = new VelocityContext(templateValues);
    context.put("date", new DateTool());
    return getTemplateString(templateName, context);
  }

  @Override
  public String evaluateTemplate(String template, Map<String, Object> templateValues) {
    VelocityContext context = new VelocityContext(templateValues);
    StringWriter w = new StringWriter();
    Velocity.evaluate(context, w, "dynamicly constructed template", template);
    return w.toString();
  }

  @Override
  public List<String> getVariableNamesFromTemplate(String template) {
    List<String> variableNames = new ArrayList<>();
    String[] splittedTemplate = template.split(Pattern.quote("${"));
    if (splittedTemplate.length > 1) {
      for (String part : splittedTemplate) {
        if (part.contains("}")) {
          variableNames.add(part.split("}")[0]);
        }
      }
    }
    return variableNames;
  }

  private String removeProtocolFromPath(String rootLocation) {
    rootLocation = rootLocation.replaceFirst("file:", "");
    if (rootLocation.matches("/.:")) {
      rootLocation = rootLocation.replaceFirst("/", "");
    } else if (rootLocation.matches("\\\\.:")) {
      rootLocation = rootLocation.replaceFirst("\\\\", "");
    }
    return rootLocation;
  }

  private String getTemplateString(String templateName, VelocityContext context) {
    StringWriter w = new StringWriter();

    Velocity.mergeTemplate(templateName, "UTF-8", context, w);
    return w.toString();
  }

  @PostConstruct
  private void initVelocity() {
    Properties p = getVelocityProperties();
    if (p != null) {
      Velocity.init(p);
    } else {
      Velocity.init();
    }
  }

  private Properties getVelocityProperties() {
    try {
      Properties p = new Properties();
      InputStream stream = TemplateServiceImpl.class.getResourceAsStream(VELOCITY_PROPERTIES_LOCATION);
      p.load(stream);
      p.put(VELOCITY_TEMPLATE_LOCATION_KEY, getTemplateLocation(p));
      return p;
    } catch (IOException e) {
      logger.warn("Could not load Velocity Properties from resource location {}. Using defaults.", VELOCITY_PROPERTIES_LOCATION);
    }
    return null;
  }

  private String getTemplateLocation(Properties p) {
    String templateLocation = p.getProperty(VELOCITY_TEMPLATE_LOCATION_KEY);
    if (templateLocation.contains("/") && !"/".equals("/")) {
      templateLocation = templateLocation.replace("/", "\\");
    } else if (templateLocation.contains("\\") && !"/".equals("\\")) {
      templateLocation = templateLocation.replace("\\", "/");
    }
    if (rootLocation == null) {
      return templateLocation;
    }
    if (rootLocation.endsWith("/") && templateLocation.startsWith("/")) {
      templateLocation = templateLocation.substring(1);
    }
    templateLocation = rootLocation + templateLocation;
    return templateLocation;
  }
}
