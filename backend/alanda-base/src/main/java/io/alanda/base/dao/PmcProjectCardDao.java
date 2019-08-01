/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.dao;

import java.util.List;

import io.alanda.base.entity.PmcProjectCard;

/**
 *
 * @author developer
 */
public interface PmcProjectCardDao extends CrudDao<PmcProjectCard> {
  
  List<PmcProjectCard> getByProjectandList(Long projectGuid, Long cardListGuid);
  
  List<PmcProjectCard> getByProjectIdandListName(String projectId, String cardListName);
  
}
