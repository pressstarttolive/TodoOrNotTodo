package com.pressstarttolive.logger.todoornottodo;

import android.graphics.Color;

/**
 * Created by logger on 12/20/14.
 */
public class ChecklistTask extends ChecklistObject{
    String mTitle;
    String mDescription;
    boolean mHasCheckbox = true;
    Integer mColor;

    public ChecklistTask(String id){
        super(id);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public boolean hasCheckbox() {
        return mHasCheckbox;
    }

    public void setHasCheckbox(boolean hasCheckbox) {
        mHasCheckbox = hasCheckbox;
    }

    public Integer getColor() {
        return mColor;
    }

    public void setColor(Integer color) {
        mColor = color;
    }
}
