/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.dto;

import java.util.List;

/**
 *
 * @author developer
 */
public class CardListDto extends CompactCardListDto {
  

  List<CardDto> cards;

  public CardListDto() {
  }

  public List<CardDto> getCards() {
    return cards;
  }

  public void setCards(List<CardDto> cards) {
    this.cards = cards;
  }
  
}
