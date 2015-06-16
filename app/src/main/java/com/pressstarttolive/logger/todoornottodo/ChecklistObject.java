package com.pressstarttolive.logger.todoornottodo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by logger on 12/20/14.
 */
public abstract class ChecklistObject {
    private String mId;
    private List<String> mChildren;
    private List<String> mRivals;

    public ChecklistObject(String id){
        mId=id;
        mChildren=new ArrayList<>();
        mRivals=new ArrayList<>();
    }

    public String getId() {
        return mId;
    }

    public List<String> getChildren() {
        return mChildren;
    }

    public List<String> getRivals() {
        return mRivals;
    }

    public void addRivals(String[] rivals){
        addRivals(Arrays.asList(rivals));
    }

    public void addChildren(String[] children){
        addChildren(Arrays.asList(children));
    }

    public void addRivals(List<String> rivals) {
        mRivals = rivals;
    }

    public void addChildren(List<String> children) {
        mChildren = children;
    }
}
