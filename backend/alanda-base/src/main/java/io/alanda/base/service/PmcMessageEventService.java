package io.alanda.base.service;


public interface PmcMessageEventService {
  void sendMessageToCatchEvent(String messageName, String processDefinitionKey, Long pmcProjectGuid);
}
