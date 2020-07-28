/**
 * 
 */
package io.alanda.base.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.connector.PmcRefObjectConnector;
import io.alanda.base.dao.PmcBlogDao;
import io.alanda.base.dao.PmcTagDao;
import io.alanda.base.dto.InternalContactDto;
import io.alanda.base.dto.PmcBlogDto;
import io.alanda.base.dto.PmcTagDto;
import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.dto.RefObject;
import io.alanda.base.dto.RefObjectDto;
import io.alanda.base.entity.PmcBlog;
import io.alanda.base.entity.PmcTag;
import io.alanda.base.service.PmcBlogService;
import io.alanda.base.service.PmcUserService;
import io.alanda.base.type.ProcessVariables;
import io.alanda.base.util.DozerMapper;
import io.alanda.base.util.UserContext;

/**
 * @author Jens Kornacker
 */
@Named("blogService")
@Stateless // (name = "pmcBlogService")
public class PmcBlogServiceImpl implements PmcBlogService, PmcRefObjectConnector {

  private static final Logger log = LoggerFactory.getLogger(PmcBlogServiceImpl.class);

  @Inject
  private DozerMapper dozerMapper;

  @Inject
  private PmcBlogDao pmcBlogDao;

  @Inject
  private PmcTagDao pmcTagDao;

  @Inject
  private RuntimeService runtimeService;

  @Inject
  private PmcUserService pmcUserService;

  //  @Inject
  //  private DocumentService documentService;

  /**
   * 
   */
  public PmcBlogServiceImpl() {
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.base.service.PmcBlogService#getBlogList()
   */
  @Override
  public List<PmcBlogDto> getBlogList(String status) {
    return getBlogList(status, 0, 20);
  }

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.base.service.PmcBlogService#getBlogList()
   */
  @Override
  public List<PmcBlogDto> getBlogList(String status, int first, int count) {
    log.info("status in BlogServiceImpl: {}", status);
    return dozerMapper.mapCollection(pmcBlogDao.getPostsByStatus(status, first, count), PmcBlogDto.class);
  }

  @Override
  public PmcBlogDto createBlogPost(PmcBlogDto blogDto) {
    Collection<PmcTagDto> tags = blogDto.getTags();
    // logger.debug("TAGS" + tags.toString());
    PmcBlog blog = dozerMapper.map(blogDto, PmcBlog.class);
    blog.setTags(new ArrayList<PmcTag>());
    if (tags != null) {
      for (PmcTagDto tagDto : tags) {
        log.info("tagDto {}", tagDto.getText());
        PmcTag pmcTag = pmcTagDao.getByText(tagDto.getText());
        if (pmcTag == null) {
          pmcTag = new PmcTag();
          pmcTag.setText(tagDto.getText());
          pmcTagDao.create(pmcTag);
        }
        blog.addTag(pmcTag);
      }
    }
    pmcBlogDao.create(blog);
    return dozerMapper.map(blog, PmcBlogDto.class);
  }

  @Override
  public PmcBlogDto updateBlog(PmcBlogDto blogDto) {
    PmcBlog pmcBlog = pmcBlogDao.getById(blogDto.getGuid());
    pmcBlog.setTitle(blogDto.getTitle());
    pmcBlog.setTeaser(blogDto.getTeaser());
    pmcBlog.setContent(blogDto.getContent());
    pmcBlog.setAuthorFirstName(blogDto.getAuthorFirstName());
    pmcBlog.setAuthorSurname(blogDto.getAuthorSurname());
    Collection<PmcTagDto> tags = blogDto.getTags();
    pmcBlog.setTags(new ArrayList<PmcTag>());
    if (tags != null) {
      for (PmcTagDto tagDto : tags) {
        log.info("tagDto {}", tagDto.getText());
        PmcTag pmcTag = pmcTagDao.getByText(tagDto.getText());
        if (pmcTag == null) {
          pmcTag = new PmcTag();
          pmcTag.setText(tagDto.getText());
          pmcTagDao.create(pmcTag);
        }
        pmcBlog.addTag(pmcTag);
      }
    }
    //PmcBlog blog = dozerMapper.map(blogDto, PmcBlog.class);
    pmcBlogDao.update(pmcBlog);
    return dozerMapper.map(pmcBlog, PmcBlogDto.class);
  }

  @Override
  public PmcBlogDto getBlogByBlogIdAndStatus(Long guid, String status) {
    log.info("status in PmcBlogSericeImpl.getBlogByIdAndStatus: ");
    log.info(guid.toString());
    log.info(status);
    return dozerMapper.map(pmcBlogDao.getPostByBlogId(guid, status), PmcBlogDto.class);
  }

  @Override
  public PmcBlogDto getBlogByGuid(Long pmcBlogGuid) {
    return dozerMapper.map(pmcBlogDao.getById(pmcBlogGuid), PmcBlogDto.class);
  }

  //  @Override
  //  public void deleteBlog(PmcBlogDto blogDto) {
  //    pmcBlogDao.delete(dozerMapper.map(blogDto, PmcBlog.class));
  //  }

  @Override
  public void deleteBlog(Long guid) {
    log.info("Deleting blog with id {}...", guid);
    PmcBlog pmcBlog = pmcBlogDao.getById(guid);
    pmcBlogDao.delete(pmcBlog);
    log.info("...Blogpost deleted {}", guid);
  }

  @Override
  public ProcessInstance startBlogPost() {
    // aktuellen User laden
    Long userid = UserContext.getUser().getGuid();
    PmcUserDto user = pmcUserService.getUserByUserId(userid);
    log.info("* * * * * * * * * * * * * * * * * * * * * * *  * * * * * * * * * * * * * * * * * * * * *");
    log.info(" * * *  PmcBlogServiceImpl.startBlogPost:   * * ");
    PmcBlog blog = new PmcBlog();
    blog.setStatus("unpublished");
    blog.setAuthorFirstName(user.getFirstName());
    blog.setAuthorSurname(user.getSurname());
    pmcBlogDao.create(blog);
    blog.setBlog_id(blog.getGuid());
    pmcBlogDao.getEntityManager().flush();
    Map<String, Object> varMap = new HashMap<String, Object>();
    varMap.put(ProcessVariables.REFOBJECTID, blog.getGuid());
    varMap.put(ProcessVariables.REFOBJECTTYPE, "BLOG");
    varMap.put(ProcessVariables.COMMENT_KEY, "commentKey-" + blog.getBlog_id());
    log.info(" *  *  *  *  *        varMap:         *  *  *  *  *  *  ");
    log.info(varMap.toString());
    ProcessInstance pi = runtimeService.startProcessInstanceByKey("blog-post-approval", "Blogpost-" + blog.getGuid(), varMap);
    return pi;

  }

  @Override
  public ProcessInstance modifyPublishedBlogPost(Long pmcBlogPostId) {
    log.info("Id im Service: {}", pmcBlogPostId);
    PmcBlog oldBlog = pmcBlogDao.getById(pmcBlogPostId);
    log.info("BlogServiceImpl: ");
    log.info(oldBlog.getGuid().toString());
    //logger.info(pmcBlogDao.atWork(oldBlog.getGuid()));
    if (pmcBlogDao.atWork(oldBlog.getGuid()) == true) {
      PmcBlog newBlog = new PmcBlog();
      newBlog.setTitle(oldBlog.getTitle());
      log.info("newBlog.getTitle(): ");
      log.info(newBlog.getTitle());
      //logger.info(newBlog.getGuid().toString());

      newBlog.setContent(oldBlog.getContent());
      //      logger.info("newBlog.getContent(): ");
      //      logger.info(newBlog.getContent());
      newBlog.setTeaser(oldBlog.getTeaser());
      //      logger.info("newBlog.getTeaser(): ");
      //      logger.info(newBlog.getTeaser());
      newBlog.setBlog_id(oldBlog.getBlog_id());
      newBlog.setAuthorFirstName(oldBlog.getAuthorFirstName());
      newBlog.setAuthorSurname(oldBlog.getAuthorSurname());
      //newBlog.setTags(oldBlog.getTags());
      Collection<PmcTag> tags = oldBlog.getTags();
      newBlog.setTags(new ArrayList<PmcTag>());
      if (tags != null) {
        for (PmcTag tag : tags) {
          //          logger.info("tagDto " + tag.getText());
          PmcTag pmcTag = pmcTagDao.getByText(tag.getText());
          if (pmcTag == null) {
            pmcTag = new PmcTag();
            pmcTag.setText(tag.getText());
            pmcTagDao.create(pmcTag);
          }
          newBlog.addTag(pmcTag);
        }
      }
      //      logger.info("newBlog.getTags().toString():");
      //      logger.info(newBlog.getTags().toString());
      newBlog.setStatus("unpublished");
      newBlog.setPredecessor_id(oldBlog.getGuid());
      newBlog = pmcBlogDao.create(newBlog);
      pmcBlogDao.getEntityManager().flush();

      //      System.out.println(newBlog.getGuid());
      //      logger.info("oldblog.guid: " + oldBlog.getGuid().toString() + "oldblog.title: " + oldBlog.getTitle().toString());
      //      logger.info("newblog.guid: " + newBlog.getGuid().toString() + "newBlog.title: " + newBlog.getTitle().toString());
      // pmcBlogDao.getEntityManager().flush();
      Map<String, Object> varMap = new HashMap<String, Object>();
      varMap.put(ProcessVariables.REFOBJECTID, newBlog.getGuid());
      varMap.put(ProcessVariables.REFOBJECTTYPE, "BLOG");
      varMap.put(ProcessVariables.COMMENT_KEY, "commentKey-" + newBlog.getBlog_id());
      ProcessInstance pi = runtimeService.startProcessInstanceByMessage("modify-blog", "Blogpost-" + newBlog.getGuid(), varMap);

      //      DocuQueryDto query = DocuQueryDto.forRefObject("Blogpost-" + oldBlog.getGuid(), "BLOG", oldBlog.getGuid(), false);
      //      DocuQueryDto newQuery = DocuQueryDto.forRefObject("Blogpost-" + newBlog.getGuid(), "BLOG", newBlog.getGuid(), false);
      //      List<DocumentSimpleDto> oldPics = documentService.getFolderContent(query);
      //      for (DocumentSimpleDto pic : oldPics) {
      //        logger.info(pic.getName());
      //        byte[] tmp = new byte[pic.getSize().intValue()];
      //        try {
      //          pic.getIntputStream().read(tmp);
      //          DocumentSimpleDto tmpDoc = documentService.store(newQuery, tmp, pic);
      //          //TODO: update link in content
      //        } catch (IOException e) {
      //          logger.error("Could not read or store file: ", e);
      //        }
      //
      //      }

      return pi;
    } else {
      return null;
    }

  }

  @Override
  public void publish(Long refObjectId) {
    log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    PmcBlog pmcBlog = pmcBlogDao.getById(refObjectId);
    log.debug(pmcBlog.getTitle());
    log.debug(pmcBlog.getGuid().toString());
    log.info("pmcBlog.getBlog_id()");
    log.info(pmcBlog.getBlog_id().toString());
    log.info(refObjectId.toString());
    if (refObjectId.longValue() != pmcBlog.getBlog_id().longValue()) {
      log.info("in if !!!!!!!");
      String status = "published";
      PmcBlog oldBlog = pmcBlogDao.getPostByBlogId(pmcBlog.getBlog_id(), status);
      //      System.out.println(oldBlog);
      log.info(oldBlog.getBlog_id().toString());
      //      System.out.println(oldBlog.getBlog_id());
      //      System.out.println(oldBlog.getTitle());
      oldBlog.setStatus("unpublished");
      pmcBlogDao.update(oldBlog);
    }
    log.info("**********************************************************************************************************");
    pmcBlog.setStatus("published");
    pmcBlogDao.update(pmcBlog);
    log.info("HIP HIP");
  }

  @Override
  public String getRefObjectType() {
    return "BLOG";
  }

  @Override
  public RefObject getRefObjectById(Long id) {
    final PmcBlog b = pmcBlogDao.getById(id);
    return new RefObject() {

      @Override
      public String getRefObjectType() {
        return "BLOG";
      }

      @Override
      public Long getRefObjectId() {
        return b.getGuid();
      }

      @Override
      public String getIdName() {
        return "BLOG-" + b.getBlog_id();
      }

      @Override
      public String getObjectName() {
        return b.getTitle();
      }
    };
  }

  @Override
  public PmcUserDto getUserForRole(Long refObjectId, String roleName) {
    PmcBlog blog = this.pmcBlogDao.getById(refObjectId);
    Long userid = blog.getUpdateUser() != null ? blog.getUpdateUser() : blog.getCreateUser();
    if (roleName.equals("author"))
      return pmcUserService.getUserByUserId(userid);
    return null;
  }

  @Override
  public List<InternalContactDto> getContacts(Long refObjectId) {
    PmcBlog blog = this.pmcBlogDao.getById(refObjectId);
    Long userid = blog.getUpdateUser() != null ? blog.getUpdateUser() : blog.getCreateUser();
    PmcUserDto user = pmcUserService.getUserByUserId(userid);
    InternalContactDto icd = new InternalContactDto(user, "author");
    return Collections.singletonList(icd);

  }

  @Override
  public RefObjectDto getRefObjectByName(String refObjectIdName) {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see com.bpmasters.pmc.base.service.PmcBlogService#getBlog()
   */

}
