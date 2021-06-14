/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.rest.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.data.domain.Sort;

import io.alanda.base.util.LabelValueBean;

/**
 *
 * @author developer
 */
public class RepoHelpers {
  
    public static Sort parseSort(Map<String, Object> sortParams, String defaultField) {
    List<Sort.Order> orders = new ArrayList<>();
    if (sortParams != null && !sortParams.isEmpty()) {
      SortedMap<Integer, LabelValueBean> sorted = orderSortOptionsByPriority(sortParams);
      for (Integer sort : sorted.keySet()) {
        LabelValueBean entry = sorted.get(sort);
        Sort.Direction dir = entry.getValue().equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        orders.add(new Sort.Order(dir, entry.getLabel()));
      }
    }
    if (orders.isEmpty()) {
      if (defaultField == null)
        return null;
      return Sort.by(Sort.Direction.ASC, defaultField);
    }
    return Sort.by(orders);
  }

  private static SortedMap<Integer, LabelValueBean> orderSortOptionsByPriority(Map<String, Object> sortOptions) {

    SortedMap<Integer, LabelValueBean> sorted = new TreeMap<>();
    for (Map.Entry<String, Object> option : sortOptions.entrySet()) {
      Map<String, Object> entry = (Map<String, Object>) option.getValue();
      Integer p = (Integer) entry.get("prio");
      LabelValueBean lvb = new LabelValueBean(option.getKey(), (String) entry.get("dir"));
      sorted.put(p, lvb);
    }
    return sorted;
  }
  
}
