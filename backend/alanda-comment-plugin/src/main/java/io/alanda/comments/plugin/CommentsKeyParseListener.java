package io.alanda.comments.plugin;

import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.bpmn.parser.AbstractBpmnParseListener;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.util.xml.Element;

/**
 * @author Petra Bierleutgeb, pb@petrabierleutgeb.io
 */
public class CommentsKeyParseListener extends AbstractBpmnParseListener {

  public CommentsKeyParseListener() {
  }

  @Override
  public void parseStartEvent(Element startEventElement, ScopeImpl scope, ActivityImpl startEventActivity) {
    startEventActivity.addListener(ExecutionListener.EVENTNAME_END, new CommentsKeyExecutionListener());
  }

}
