/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.security;

import com.google.common.cache.CacheBuilder;
import java.util.Collection;
import java.util.Set;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import java.util.concurrent.TimeUnit;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author stephan
 */
public class AuthCache<K,V> implements Cache<K,V> {
    
    private com.google.common.cache.Cache<K,V> authCache;

    public AuthCache() {
        this.authCache = CacheBuilder
                .newBuilder()
                .maximumSize(5000)
                .recordStats()
                .expireAfterAccess(1, TimeUnit.DAYS)
                .build();
    }
      

    @Override
    public V get(K k) throws CacheException {
        return authCache.getIfPresent(k);
    }

    @Override
    public V put(K k, V v) throws CacheException {
        return authCache.asMap().put(k, v);
    }

    @Override
    public V remove(K k) throws CacheException {
        return authCache.asMap().remove(k);
    }

    @Override
    public void clear() throws CacheException {
        authCache.asMap().clear();
    }

    @Override
    public int size() {
        return authCache.asMap().size();
    }

    @Override
    public Set<K> keys() {
        return authCache.asMap().keySet();
    }

    @Override
    public Collection<V> values() {
        return authCache.asMap().values();
    }
    
}
