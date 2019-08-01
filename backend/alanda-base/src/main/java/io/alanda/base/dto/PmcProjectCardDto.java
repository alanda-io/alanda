/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 *
 * @author developer
 */
public class PmcProjectCardDto {

  private Long guid;
  
  private PmcProjectCompactDto project;
  
  private CompactCardListDto cardList;
  
  private CardDto card;
  
  private String status;
  
  private String comments;
  
  private String category;
  
  private String owner;

  private String createUser;
  
  private String updateUser;
  
  @JsonFormat(pattern = "yyyy-MM-dd",timezone="Europe/Vienna")
  private Date lastUpdate;
  
  
  public PmcProjectCardDto() {
  }

  public Long getGuid() {
    return guid;
  }

  public void setGuid(Long guid) {
    this.guid = guid;
  }

  public PmcProjectCompactDto getProject() {
    return project;
  }

  public void setProject(PmcProjectCompactDto project) {
    this.project = project;
  }

  public CompactCardListDto getCardList() {
    return cardList;
  }

  public void setCardList(CompactCardListDto cardList) {
    this.cardList = cardList;
  }

  public CardDto getCard() {
    return card;
  }

  public void setCard(CardDto card) {
    this.card = card;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public String getUpdateUser() {
    return updateUser;
  }

  public void setUpdateUser(String updateUser) {
    this.updateUser = updateUser;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public Date getLastUpdate() {
    return lastUpdate;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public String getCreateUser() {
    return createUser;
  }

  public void setCreateUser(String createUser) {
    this.createUser = createUser;
  }
  
}
