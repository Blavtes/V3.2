package com.togic.launcher.util.image.impl;

import com.togic.launcher.util.image.CacheFullRemoveType;
import com.togic.launcher.util.image.CacheObject;
import com.togic.launcher.util.image.util.ObjectUtils;

/**
 * Remove type when cache is full.<br/>
 * when cache is full, compare data of object in cache, if data is smaller remove it first.<br/>
 * 
 * @author Trinea 2011-12-26
 */
public class RemoveTypeDataSmall<T> implements CacheFullRemoveType<T> {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(CacheObject<T> obj1, CacheObject<T> obj2) {
        return ObjectUtils.compare(obj1.getData(), obj2.getData());
    }
}
