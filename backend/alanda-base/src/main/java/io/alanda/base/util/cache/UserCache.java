package io.alanda.base.util.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.dto.PmcGroupDto;
import io.alanda.base.dto.PmcUserDto;
import io.alanda.base.service.PmcUserService;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;

/**
 * Cache for users
 * 
 * @author Julian Löffelhardt, julian@austria.fm
 */
@ApplicationScoped
public class UserCache {

  @Inject
  private PmcUserService pmcUserService;

  private static final Logger log = LoggerFactory.getLogger(UserCache.class);

  private LoadingCache<String, Optional<PmcUserDto>> usernameCache;

  private LoadingCache<Long, Optional<PmcUserDto>> guidCache;

  private LoadingCache<Long, Optional<PmcGroupDto>> groupCache;

  public UserCache() {
    usernameCache = CacheBuilder
      .newBuilder()
      .maximumSize(1000)
      .recordStats()
      .expireAfterWrite(10, TimeUnit.MINUTES)
      .<String, Optional<PmcUserDto>> build(new CacheLoader<String, Optional<PmcUserDto>>() {

        @Override
        public Optional<PmcUserDto> load(String key) throws Exception {
          PmcUserDto u = UserCache.this.pmcUserService.getUserByLoginName(key, true);
          if (u != null)
            putInGuidCache(u);
          return Optional.fromNullable(u);
        }
      });
    guidCache = CacheBuilder
      .newBuilder()
      .maximumSize(1000)
      .recordStats()
      .expireAfterWrite(10, TimeUnit.MINUTES)
      .<Long, Optional<PmcUserDto>> build(new CacheLoader<Long, Optional<PmcUserDto>>() {

        @Override
        public Optional<PmcUserDto> load(Long guid) throws Exception {
          return Optional.fromNullable(UserCache.this.pmcUserService.getUserByUserId(guid));
        }
      });
    groupCache = CacheBuilder
      .newBuilder()
      .maximumSize(1000)
      .recordStats()
      .expireAfterWrite(10, TimeUnit.MINUTES)
      .<Long, Optional<PmcGroupDto>> build(new CacheLoader<Long, Optional<PmcGroupDto>>() {

        @Override
        public Optional<PmcGroupDto> load(Long key) throws Exception {
          PmcGroupDto g = UserCache.this.pmcUserService.getGroupById(key);
          return Optional.fromNullable(g);
        }
      });

  }

  private void putInUsernameCache(PmcUserDto user) {
    this.usernameCache.put(user.getLoginName(), Optional.fromNullable(user));
  }

  private void putInGuidCache(PmcUserDto user) {
    this.guidCache.put(user.getGuid(), Optional.fromNullable(user));

  }

  /**
   * @param loginName the loginName of the user
   * @return the loaded user or null if an error occurred
   */
  public PmcUserDto get(String loginName) {
    try {
      return usernameCache.get(loginName).orNull();
    } catch (ExecutionException e) {
      log.warn("An exception occurred while reading from user cache", e.getCause());
      return null;
    }
  }

  /**
   * Invalidates the user in the userCache
   * 
   * @param loginName the loginName of the user
   */
  public void invalidate(String loginName) {
    usernameCache.invalidate(loginName);
  }

  /**
   * @param pmcUserGuid the guid of the user
   * @return the loaded user or null if an error occurred
   */
  public PmcUserDto get(Long pmcUserGuid) {
    try {
      return guidCache.get(pmcUserGuid).orNull();
    } catch (ExecutionException e) {
      log.warn("An exception occurred while reading from user cache", e.getCause());
      return null;
    }
  }

  /**
   * @param pmcGroupGuid the guid of the group
   * @return the loaded user or null if an error occurred
   */
  public PmcGroupDto getGroup(Long pmcGroupGuid) {
    try {
      return groupCache.get(pmcGroupGuid).orNull();
    } catch (ExecutionException e) {
      log.warn("An exception occurred while reading from group cache", e.getCause());
      return null;
    }
  }

  public Map<String, CacheStats> getStats() {
    CacheStats stats = usernameCache.stats();
    CacheStats guidStats = guidCache.stats();
    Map<String, CacheStats> map = new HashMap<>();
    map.put("username", stats);
    map.put("guid", guidStats);
    map.put("group", groupCache.stats());
    return map;
  }

  public void invalidateAll() {
    usernameCache.invalidateAll();
    guidCache.invalidateAll();
  }
}
