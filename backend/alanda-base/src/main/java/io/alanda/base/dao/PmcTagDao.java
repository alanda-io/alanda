package io.alanda.base.dao;

import java.util.List;

import io.alanda.base.entity.PmcTag;

public interface PmcTagDao extends CrudDao<PmcTag> {

  PmcTag getByText(String text);

  List<PmcTag> getTagList(String query);

}
