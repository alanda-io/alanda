/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author developer
 */
@Entity
@Table(name = "PMC_CARDLIST")
@NamedQueries({@NamedQuery(name = "CardList.getListByName", query = "Select l from CardList l where l.idName = :idName")})
public class CardList extends AbstractEntity implements Serializable {
  
  private static final long serialVersionUID = 2198958328207035743L;
  
  private String idName;
  
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "PMC_CARDLIST_CARD", joinColumns = @JoinColumn(name = "REF_CARDLIST", referencedColumnName = "GUID"), inverseJoinColumns = @JoinColumn(name = "REF_CARD", referencedColumnName = "GUID"))
  private List<Card> cards;

  public CardList() {
  }

  public String getIdName() {
    return idName;
  }

  public void setIdName(String idName) {
    this.idName = idName;
  }

  public List<Card> getCards() {
    return cards;
  }

  public void setCards(List<Card> cards) {
    this.cards = cards;
  }
  
  
}
