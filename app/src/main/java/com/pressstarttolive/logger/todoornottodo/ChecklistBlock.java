package com.pressstarttolive.logger.todoornottodo;

/**
 * Created by logger on 12/20/14.
 */
public class ChecklistBlock extends ChecklistObject{
    private int mCount;

    public ChecklistBlock(String id){
        super(id);
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        mCount = count;
    }
}
