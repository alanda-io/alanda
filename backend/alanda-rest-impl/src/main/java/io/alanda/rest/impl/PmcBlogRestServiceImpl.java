package io.alanda.rest.impl;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.PmcBlogDto;
import io.alanda.base.dto.ProcessDto;
import io.alanda.base.service.PmcBlogService;

import io.alanda.rest.PmcBlogRestService;

public class PmcBlogRestServiceImpl implements PmcBlogRestService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Inject
  private PmcBlogService pmcBlogService;

  @Override
  public List<PmcBlogDto> getPosts(String status, int first, int count) {
    logger.info("status in RestServiceImpl: " + status);
    logger.info("first in RestServiceImpl: " + first);
    logger.info("count in RestServiceImpl: " + count);
    List<PmcBlogDto> pmcBlogDtos = pmcBlogService.getBlogList(status, first, count);
    for (PmcBlogDto blog : pmcBlogDtos) {
      blog.setContent(null);
    }
    return pmcBlogDtos;
  }

  @Override
  public PmcBlogDto getSingleBlogByBlogIdAndStatus(Long pmcBlogPostId, String status) {
    PmcBlogDto pmcBlogDto = pmcBlogService.getBlogByBlogIdAndStatus(pmcBlogPostId, status);
    return pmcBlogDto;
  }

  @Override
  public PmcBlogDto getSingleBlogByGuid(Long pmcBlogGuid) {
    PmcBlogDto pmcBlogDto = pmcBlogService.getBlogByGuid(pmcBlogGuid);
    return pmcBlogDto;
  }

  @Override
  public Response startBlogPost() {
    ProcessInstance pi = pmcBlogService.startBlogPost();
    ProcessDto dto = new ProcessDto();
    dto.setProcessInstanceId(pi.getProcessInstanceId());
    return new ResponseBuilderImpl().entity(dto).status(Status.CREATED).build();
  }

  @Override
  public Response createBlogPost(PmcBlogDto pmcBlogDto) {
    pmcBlogDto = pmcBlogService.createBlogPost(pmcBlogDto);
    return new ResponseBuilderImpl().entity(pmcBlogDto).status(Status.CREATED).build();
  }

  @Override
  public PmcBlogDto updateBlogPost(PmcBlogDto pmcBlogDto) {
    logger.info("  *  *  *  *  *  *  *  *  *  *  *  *  * ");
    logger.info(pmcBlogDto.getAuthorFirstName());
    pmcBlogService.updateBlog(pmcBlogDto);
    return pmcBlogDto;
  }

  //  @Override
  //  public void deleteBlogPost(Long pmcBlogPostId) {
  //    PmcBlogDto blogDto = pmcBlogService.getBlog(pmcBlogPostId);
  //    pmcBlogService.deleteBlog(blogDto);
  //  }

  @Override
  public Response deleteBlogPost(Long pmcBlogPostId) {
    pmcBlogService.deleteBlog(pmcBlogPostId);
    return Response.ok().build();
  }

  @Override
  public Response modifyPublishedBLogPost(Long pmcBlogPostId) {
    logger.info("pmcBlogPostId: " + pmcBlogPostId.toString());
    ProcessInstance pi;
    logger.info("* * * * * * PmcBlogRestServiceImpl.modifyPublishedBlogPost * * * * *");
    pi = pmcBlogService.modifyPublishedBlogPost(pmcBlogPostId);
    if (pi != null) {
      logger.info("* * * *  in if:  ");
      logger.info("pi pi pi pi: " + pi.toString());
      ProcessDto processDto = new ProcessDto();
      processDto.setProcessInstanceId(pi.getProcessInstanceId());
      return new ResponseBuilderImpl().entity(processDto).status(Status.CREATED).build();
    } else {
      logger.info("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
      //      logger.debug(pi.toString());
      logger.info("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
      return new ResponseBuilderImpl().status(304).build();
    }
  }

}
