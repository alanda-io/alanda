package io.alanda.comments.plugin;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.impl.bpmn.parser.BpmnParseListener;
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;

/**
 * @author Petra Bierleutgeb, pb@petrabierleutgeb.io
 */
public class CommentsKeyProcessEnginePlugin extends AbstractProcessEnginePlugin {

  @Override
  public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
    List<BpmnParseListener> preParseListeners = processEngineConfiguration.getCustomPreBPMNParseListeners();

    if (preParseListeners == null) {
      preParseListeners = new ArrayList<>();
      processEngineConfiguration.setCustomPreBPMNParseListeners(preParseListeners);
    }

    preParseListeners.add(new CommentsKeyParseListener());
  }

}
