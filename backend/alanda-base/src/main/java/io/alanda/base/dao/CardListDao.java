/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.dao;

import io.alanda.base.entity.CardList;

/**
 *
 * @author developer
 */
public interface CardListDao extends CrudDao<CardList>{
  
  CardList getCardListByIdName(String idName);
  
}
