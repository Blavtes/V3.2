package com.togic.launcher.util.image.impl;

import com.togic.launcher.util.image.CacheFullRemoveType;
import com.togic.launcher.util.image.CacheObject;

/**
 * Remove type when cache is full.<br/>
 * when cache is full, compare enter time of object in cache, if time is smaller remove it first. also FIFO<br/>
 * 
 * @author Trinea 2011-12-26
 */
public class RemoveTypeEnterTimeFirst<T> implements CacheFullRemoveType<T> {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(CacheObject<T> obj1, CacheObject<T> obj2) {
        return (obj1.getEnterTime() > obj2.getEnterTime()) ? 1
            : ((obj1.getEnterTime() == obj2.getEnterTime()) ? 0 : -1);
    }
}
