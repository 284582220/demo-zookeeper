package com.yangguojun.curator;

import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

/**
 * Created by yangguojun on 2017/12/13.
 */
public class PathEvent {
    private PathChildrenCacheEvent.Type type;
    private String path;
    private String value;

    public PathChildrenCacheEvent.Type getType() {
        return type;
    }

    public void setType(PathChildrenCacheEvent.Type type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
