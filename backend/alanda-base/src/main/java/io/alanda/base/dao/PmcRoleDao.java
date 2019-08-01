/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.dao;

import io.alanda.base.entity.PmcRole;

/**
 *
 * @author developer
 */
public interface PmcRoleDao extends CrudDao<PmcRole>{
  
  PmcRole getRoleByName(String name);
  
}
