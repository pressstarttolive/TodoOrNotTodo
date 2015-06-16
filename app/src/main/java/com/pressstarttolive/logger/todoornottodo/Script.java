package com.pressstarttolive.logger.todoornottodo;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by logger on 12/20/14.
 */
public class Script {
    HashMap<String,ChecklistObject> mChecklistObjects;
    String mOrigin;
    boolean savePruned;
    boolean saveCompleted;
    Integer mForegroundColor;
    Integer mBackgroundColor;

    public Script(){
        mChecklistObjects = new HashMap<>();
    }

    public void addChecklistObject (ChecklistObject checklistObject){
        Log.d("Script add obj ",checklistObject.getId());
        if (mChecklistObjects.containsKey(checklistObject.getId())){
            throw new IllegalStateException("Trying to add an ID that already exists in the script");
        }
        mChecklistObjects.put(checklistObject.getId(),checklistObject);
    }

    public ChecklistObject getChecklistObject(String id){
        return mChecklistObjects.get(id);
    }

    public HashMap<String,ChecklistObject> getChecklistObjects(){
        return new HashMap<>(mChecklistObjects);
    }

    public String getOrigin() {
        return mOrigin;
    }

    public void setOrigin(String origin) {
        mOrigin = origin;
    }

    public Integer getForegroundColor() {
        return mForegroundColor;
    }

    public void setForegroundColor(Integer color) {
        mForegroundColor = color;
    }

    public Integer getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBackgroundColor(Integer backgroundColor) {
        mBackgroundColor = backgroundColor;
    }
}
