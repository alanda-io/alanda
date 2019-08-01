package io.alanda.base.dao;

import java.util.List;

import io.alanda.base.entity.PmcBlog;

public interface PmcBlogDao extends CrudDao<PmcBlog> {

  List<PmcBlog> getPostsByStatus(String status, int first, int count);

  boolean atWork(Long oldBlogId);

  PmcBlog getPostByBlogId(Long blog_id, String status);

}
