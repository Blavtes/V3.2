/**
 * Copyright 2009 Joe LaPenna
 */

package com.togic.remote.types;

import java.util.ArrayList;

/**
 * @author libin
 */
public class Group<T extends RemoteType> extends ArrayList<T> implements RemoteType {

    private static final long serialVersionUID = 1L;

    private String mType;

    public void setType(String type) {
        mType = type;
    }

    public String getType() {
        return mType;
    }
}
