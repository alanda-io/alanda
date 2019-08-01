package io.alanda.base.service;

import java.util.List;
import java.util.Map;

public interface TemplateService {

  /**
   * Get a Template String which is filled with the given data.
   * <p>
   * Example: <br>
   * Key: object <br>
   * Access in template: $object.property <br>
   * 
   * @param templateName Name of the template which will be used
   * @param key The key for the POJO
   * @param value A POJO which will be used to fill the template data
   * @return
   */
  String getTemplateString(String templateName, String key, Object value);

  /**
   * Get a Template String which is filled with the given data.
   * <p>
   * Example: <br>
   * MapEntry1: Key=object, Value=someValue <br>
   * MapEntry2: Key=pojo, Value=somePojo <br>
   * Access in template: $object and $pojo.property <br>
   * 
   * @param templateName Name of the template, which will be used
   * @param templateValues Map for template values. The value of the map can be a String or POJO
   * @return
   */
  String getTemplateString(String templateName, Map<String, Object> templateValues);

  /**
   * Applies {@code templateValues} to the input string {@code template}. To be used when a template is not read from a
   * file.
   * 
   * @param template
   * @param templateValues
   * @return
   */
  String evaluateTemplate(String template, Map<String, Object> templateValues);

  List<String> getVariableNamesFromTemplate(String template);

}
