package com.yangguojun.curator;

/**
 * Created by yangguojun on 2017/12/22.
 */
public class NodeEvent {
    public static final int NODE_DELETE = 1;
    public static final int NODE_ADD_OR_UPDATE = 2;

    private int type;
    private String value;
    private int version;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
