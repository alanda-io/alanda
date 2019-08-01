/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author developer
 */
@Entity
@Table(name = "PMC_PROJECTCARD")
public class PmcProjectCard extends AbstractAuditEntity implements Serializable {
  
  private static final long serialVersionUID = -8053887339773761528L;
  
  private String comments;
  
  private String status;
  
  private String category;
  
  private String owner;
  
  @ManyToOne
  @JoinColumn(name = "REF_PROJECT")
  private PmcProject project;

  @ManyToOne
  @JoinColumn(name = "REF_CARDLIST")
  private CardList cardList;
  
  @ManyToOne
  @JoinColumn(name = "REF_CARD")
  private Card card;

  public PmcProjectCard() {
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public PmcProject getProject() {
    return project;
  }

  public void setProject(PmcProject project) {
    this.project = project;
  }

  public CardList getCardList() {
    return cardList;
  }

  public void setCardList(CardList cardList) {
    this.cardList = cardList;
  }
  
  public Card getCard() {
    return card;
  }

  public void setCard(Card card) {
    this.card = card;
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
  
}
