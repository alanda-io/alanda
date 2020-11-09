/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.alanda.base.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

/**
 *
 * @author stephan
 */
public class AuthCacheManager implements CacheManager {
    
    private Map<String, Cache> authCaches;

    public AuthCacheManager() {
        authCaches = new ConcurrentHashMap<>();
    }
    
    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        if (!authCaches.containsKey(name)) {
            AuthCache<K,V> authCache = new AuthCache<>();
            authCaches.put(name, authCache);
        }
        return authCaches.get(name);
    }
    
}
