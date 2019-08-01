/**
 * 
 */
package io.alanda.rest;

import java.io.Serializable;
import java.util.Map;

/**
 * @author jlo
 */
public class PagedSearchOptions implements Serializable {

  Integer pageNumber;

  Integer pageSize;

  Map<String, Object> filterOptions;

  Map<String, Object> sortOptions;

  /**
   * 
   */
  public PagedSearchOptions() {
    // TODO Auto-generated constructor stub
  }

  public Integer getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public Map<String, Object> getFilterOptions() {
    return filterOptions;
  }

  public void setFilterOptions(Map<String, Object> filterOptions) {
    this.filterOptions = filterOptions;
  }

  public Map<String, Object> getSortOptions() {
    return sortOptions;
  }

  public void setSortOptions(Map<String, Object> sortOptions) {
    this.sortOptions = sortOptions;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((filterOptions == null) ? 0 : filterOptions.hashCode());
    result = prime * result + ((pageNumber == null) ? 0 : pageNumber.hashCode());
    result = prime * result + ((pageSize == null) ? 0 : pageSize.hashCode());
    result = prime * result + ((sortOptions == null) ? 0 : sortOptions.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PagedSearchOptions other = (PagedSearchOptions) obj;
    if (filterOptions == null) {
      if (other.filterOptions != null)
        return false;
    } else if ( !filterOptions.equals(other.filterOptions))
      return false;
    if (pageNumber == null) {
      if (other.pageNumber != null)
        return false;
    } else if ( !pageNumber.equals(other.pageNumber))
      return false;
    if (pageSize == null) {
      if (other.pageSize != null)
        return false;
    } else if ( !pageSize.equals(other.pageSize))
      return false;
    if (sortOptions == null) {
      if (other.sortOptions != null)
        return false;
    } else if ( !sortOptions.equals(other.sortOptions))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "PagedSearchOptions [pageNumber=" +
      pageNumber +
      ", pageSize=" +
      pageSize +
      ", filterOptions=" +
      filterOptions +
      ", sortOptions=" +
      sortOptions +
      "]";
  }

}
