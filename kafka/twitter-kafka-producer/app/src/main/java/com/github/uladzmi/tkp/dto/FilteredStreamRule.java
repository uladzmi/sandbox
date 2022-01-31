package com.github.uladzmi.tkp.dto;

public class FilteredStreamRule {
    private String tag;
    private String value;

    public String getTag() {
        return tag;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "FilteredStreamRule{" + "tag='" + tag + '\'' + ", value='" + value + '\'' +'}';
    }
}
