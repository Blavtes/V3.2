package com.togic.launcher.util.image.impl;

import com.togic.launcher.util.image.CacheFullRemoveType;
import com.togic.launcher.util.image.CacheObject;

/**
 * Remove type when cache is full.<br/>
 * when cache is full, compare priority of object in cache, if priority is higher remove it first.<br/>
 * 
 * @author Trinea 2011-12-26
 */
public class RemoveTypePriorityHigh<T> implements CacheFullRemoveType<T> {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(CacheObject<T> obj1, CacheObject<T> obj2) {
        return (obj2.getPriority() > obj1.getPriority()) ? 1 : ((obj2.getPriority() == obj1.getPriority()) ? 0 : -1);
    }
}
