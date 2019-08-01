package io.alanda.base.service;

import java.util.List;

import io.alanda.base.dto.PmcCommentDto;

public interface PmcCommentService {
  
  public List<PmcCommentDto> getAllForProcessInstanceId(String processInstanceId);
  
  public PmcCommentDto insert(PmcCommentDto pmcCommentDto);
  
  public List<PmcCommentDto> getAllForProcessInstanceIdAndRefObjectId(String processInstanceId, long refObjectId);

}
