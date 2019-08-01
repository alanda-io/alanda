/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author developer
 */
@Entity
@Table(name = "PMC_CARD")
public class Card extends AbstractEntity implements Serializable {
  
  private static final long serialVersionUID = 2218184263127013269L;
  
  @Column(name = "TITLE")
  String title;

  public Card() {
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
  
  
  
}
