package io.alanda.base.service;

import java.util.List;

import org.camunda.bpm.engine.runtime.ProcessInstance;

import io.alanda.base.dto.PmcBlogDto;

public interface PmcBlogService {

  public PmcBlogDto createBlogPost(PmcBlogDto blogDto);

  public List<PmcBlogDto> getBlogList(String status);

  public PmcBlogDto getBlogByBlogIdAndStatus(Long guid, String status);

  public PmcBlogDto updateBlog(PmcBlogDto blogDto);

  //public void deleteBlog(PmcBlogDto blogDto);
  public void deleteBlog(Long guid);

  public ProcessInstance startBlogPost();

  public void publish(Long refObjectId);

  public ProcessInstance modifyPublishedBlogPost(Long pmcBlogPostId);

  public PmcBlogDto getBlogByGuid(Long pmcBlogGuid);

  List<PmcBlogDto> getBlogList(String status, int first, int count);

  //ProcessInstance modifyPublishedBlogPost(Long pmcBlogPostId);

}
