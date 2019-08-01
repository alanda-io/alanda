package io.alanda.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dao.PmcCommentDao;
import io.alanda.base.dto.PmcCommentDto;
import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.entity.PmcComment;
import io.alanda.base.service.PmcCommentService;
import io.alanda.base.util.DozerMapper;
import io.alanda.base.util.UserContext;
import io.alanda.base.util.cache.UserCache;

@Stateless
@Named("pmcCommentService")
public class PmcCommentServiceImpl implements PmcCommentService {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Inject
  private PmcCommentDao pmcCommentDao;

  @Inject
  private DozerMapper dozerMapper;

  @Inject
  private UserCache userCache;

  @Override
  public List<PmcCommentDto> getAllForProcessInstanceId(String processInstanceId) {
    List<PmcCommentDto> retVal = dozerMapper
      .mapCollection(pmcCommentDao.getAllForProcessInstanceId(processInstanceId), PmcCommentDto.class);
    retVal = parseComments(retVal);
    return retVal;
  }

  @Override
  public PmcCommentDto insert(PmcCommentDto pmcCommentDto) {
    logger.info(pmcCommentDto.getSubject());
    logger.info(pmcCommentDto.getText());
    pmcCommentDto.setCreateDate(new Date());
    pmcCommentDto.setUpdateDate(new Date());
    pmcCommentDto.setCreateUser(UserContext.getUser().getGuid());
    pmcCommentDto.setUpdateUser(UserContext.getUser().getGuid());
    PmcComment pmcComment = dozerMapper.map(pmcCommentDto, PmcComment.class);
    pmcCommentDao.insert(pmcComment);
    return dozerMapper.map(pmcComment, PmcCommentDto.class);
  }

  @Override
  public List<PmcCommentDto> getAllForProcessInstanceIdAndRefObjectId(String processInstanceId, long refObjectId) {
    List<PmcCommentDto> retVal = dozerMapper
      .mapCollection(pmcCommentDao.getAllForProcessInstanceIdAndRefObjectId(processInstanceId, refObjectId), PmcCommentDto.class);
    retVal = parseComments(retVal);
    return retVal;
  }

  private List<PmcCommentDto> parseComments(List<PmcCommentDto> comments) {
    List<PmcCommentDto> retVal = new ArrayList<PmcCommentDto>();
    Map<Long, PmcCommentDto> map = new HashMap<Long, PmcCommentDto>();
    for (PmcCommentDto c : comments) {

      //set Author
      PmcUserDto user = userCache.get(c.getCreateUser());
      String name = "---";
      if (user != null) {
        name = user.getFirstName() + " " + user.getSurname();
      }
      c.setAuthorName(name);

      // create comment threads
      if (c.getReplyTo() == null) {
        map.put(c.getGuid(), c);
        retVal.add(c);
      } else {
        PmcCommentDto parent = map.get(c.getReplyTo());
        if (parent == null) {
          logger.warn("Parent not found for comment: " + c.getGuid());
          continue;
        }

        parent.getReplies().add(c);
      }
    }
    return retVal;
  }

}
