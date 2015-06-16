package com.pressstarttolive.logger.todoornottodo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by logger on 12/20/14.
 */
public class ChecklistFragment extends Fragment{
    ListView mCheckList;
    BaseAdapter mCheckListAdapter;
    List<ChecklistTask> mTasks;
    Set<String> mPrunedIds;
    HashMap<String,Integer> mBlockCounts;
    HashMap<String,ChecklistBlock> mBlocks;
    //String mLastCheckedTask;
    Set<String> mPreviouslyCheckedIds;
    Script mScript;


    public ChecklistFragment() {
        blankLists();
    }

    private void blankLists(){
        mPreviouslyCheckedIds = new HashSet<>();
        mTasks = new ArrayList<>();
        mBlocks = new HashMap<>();
        mBlockCounts = new HashMap<>();
        mPrunedIds = new HashSet<>();
    }

    public void setScript(Script script){
        mScript=script;
        if (script.getBackgroundColor()!=null){
            mCheckList.setBackgroundColor(script.getBackgroundColor());
        }
        ChecklistObject origin = mScript.getChecklistObject(mScript.getOrigin());
        if (origin instanceof ChecklistTask){
            mTasks.add((ChecklistTask) origin);
        } else {
            addChecklistObjects(origin.getChildren());
        }
    }

    private void addChecklistObjects(List<String> objectIds){
        for (String objectId : objectIds){
            ChecklistObject object = mScript.getChecklistObject(objectId);
            if (object instanceof ChecklistTask){
                mTasks.add((ChecklistTask) object);
            } else if (object instanceof ChecklistBlock){
                if (mBlocks.containsKey(objectId)){
                    mBlockCounts.put(objectId,mBlockCounts.get(objectId)+1);
                    if (mBlockCounts.get(objectId)>=((ChecklistBlock)object).getCount()){
                        addChecklistObjects(object.getChildren());
                        pruneChecklistObjects(object.getRivals());
                        mBlocks.remove(objectId);
                        mBlockCounts.remove(objectId);
                    }
                } else {
                    mBlocks.put(objectId, (ChecklistBlock) object);
                    mBlockCounts.put(objectId,1);
                }
            } else {
                throw new IllegalArgumentException("ChecklistObject is neither a task nor a block");
            }
        }
        mCheckList.post(new Runnable() {
            @Override
            public void run() {
                mCheckList.smoothScrollToPosition(mCheckListAdapter.getCount() - 1);
            }
        });

    }

    private void pruneChecklistObjects(List<String> objectIds){
        for (String objectId : mPrunedIds){
            if (mBlocks.containsKey(objectId)){
                mBlocks.remove(objectId);
                mBlockCounts.remove(objectId);
            }
        }
        List<ChecklistTask> newTaskList = new ArrayList<>();
        for (ChecklistTask task : mTasks){
//            if (!mPrunedIds.contains(task.getId()) && !mPreviouslyCheckedIds.contains(task.getId())){
//                newTaskList.add(task);
//            }
            if ((mScript.saveCompleted || !mPreviouslyCheckedIds.contains(task.getId()))&&
                    (mScript.savePruned || !mPrunedIds.contains(task.getId()))){
                newTaskList.add(task);
            }
        }
        mTasks = newTaskList;
        if (mScript.savePruned){
            mPrunedIds.addAll(objectIds);
        } else {
            mPrunedIds = new HashSet<>(objectIds);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_checklist, container, false);
        setHasOptionsMenu(true);
        mCheckList = (ListView) rootView.findViewById(R.id.checklist_list);
        mCheckListAdapter = new ChecklistAdapter();
        mCheckList.setAdapter(mCheckListAdapter);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.reset_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.reset_item){
            blankLists();
            setScript(mScript);
            mCheckListAdapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    private class ChecklistAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mTasks.size();
        }

        @Override
        public ChecklistTask getItem(int position) {
            return mTasks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final ChecklistTask task = mTasks.get(position);
            if (convertView==null){
                convertView = inflater.inflate(R.layout.item_checklist,null);
            }
            TextView titleView = (TextView) convertView.findViewById(R.id.checklist_item_title);
            titleView.setText(task.getTitle());
            TextView descView = (TextView) convertView.findViewById(R.id.checklist_item_description);
            descView.setText(task.getDescription());
            ImageView checkImage = (ImageView) convertView.findViewById(R.id.check_image);
            View card = convertView.findViewById(R.id.checklist_item_card);
            if (mPreviouslyCheckedIds.contains(task.getId())){
                checkImage.setImageResource(R.drawable.checked_todo);
                convertView.setOnClickListener(null);
            } else if (mPrunedIds.contains(task.getId())){
                checkImage.setImageResource(R.drawable.pruned_todo);
                convertView.setOnClickListener(null);
            } else {
                checkImage.setImageResource(R.drawable.blank_todo);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addChecklistObjects(task.getChildren());
                        pruneChecklistObjects(task.getRivals());
                        if (!mScript.saveCompleted){
                            mPreviouslyCheckedIds.clear();
                        }
                        mPreviouslyCheckedIds.add(task.getId());
                        mCheckListAdapter.notifyDataSetChanged();
                    }
                });
            }
            if (!task.hasCheckbox()){
                checkImage.setVisibility(View.INVISIBLE);
                convertView.setOnClickListener(null);
            }
            if (task.getId().equals("9")){
                task.getId();
            }
            if (task.getColor()!=null) {
                card.setBackgroundColor(task.getColor());
            } else if (mScript.getForegroundColor()!=null) {
                card.setBackgroundColor(mScript.getForegroundColor());
            } else {
                card.setBackgroundResource(R.color.todo_light_amber);
            }
            return convertView;
        }
    }
}
