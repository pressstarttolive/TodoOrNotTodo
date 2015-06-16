package com.pressstarttolive.logger.todoornottodo;

import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by logger on 12/27/14.
 */
public class ValidationUtils {
    public static boolean mainSafetyCheck(Script script){
        boolean obviousLoops = checkForObviousLoops(script);
        boolean nonMutualPruning = checkForNonMutualPruning(script);
        boolean overDeterminedObjectsOrOrphans = checkForOverDeterminedObjectsAndOrphans(script);
        return obviousLoops&&nonMutualPruning&&overDeterminedObjectsOrOrphans;
    }

    public static boolean checkForObviousLoops(Script script){
        return obviousLoopsHelper(script.getChecklistObject(script.getOrigin()),script,new HashSet<String>());
    }

    private static boolean obviousLoopsHelper(ChecklistObject object, Script script, HashSet<String> seenObjects){
        if (seenObjects.contains(object.getId())){
            Log.w("ValidationUtils.obviousLoopsHelper",object.getId() + " is part of an obvious loop.");
            return true;
        }
        HashSet<String> newSeenObjects = new HashSet<>(seenObjects);
        newSeenObjects.add(object.getId());
        for (String childId : object.getChildren()){
            if (obviousLoopsHelper(script.getChecklistObject(childId),script,newSeenObjects)){
                return true;
            }
        }
        return false;
    }

    public static boolean checkForNonMutualPruning(Script script){
        boolean noNonMutualPruning = false;
        for (String objectId : script.getChecklistObjects().keySet()){
            for (String prunedId : script.getChecklistObject(objectId).getRivals()){
                if (!script.getChecklistObject(prunedId).getRivals().contains(objectId)){
                    noNonMutualPruning = true;
                    Log.e("ValidationUtils.checkForNonMutualPruning",objectId + " prunes " + prunedId + " but is not pruned by it.");
                }
            }
        }
        return noNonMutualPruning;
    }

    public static boolean checkForOverDeterminedObjectsAndOrphans(Script script){
        boolean noOverdeterminedObjectsOrOrphans = false;
        HashMap<String,Integer> triggeredElements = new HashMap<>();
        for (ChecklistObject checklistObject : script.getChecklistObjects().values()){
            for (String childId : checklistObject.getChildren()){
                if (triggeredElements.containsKey(childId)){
                    triggeredElements.put(childId,triggeredElements.get(childId)+1);
                } else {
                    triggeredElements.put(childId,1);
                }
            }
        }
        for (ChecklistObject checklistObject : script.getChecklistObjects().values()){
            String id = checklistObject.getId();
            if (!triggeredElements.containsKey(id)) {
                Log.e("ValidationUtils.checkForOverDeterminedObjects",id + " is an orphan");
            } else if (checklistObject instanceof ChecklistBlock){
                if (((ChecklistBlock) checklistObject).getCount()*2<=triggeredElements.get(id)){
                    noOverdeterminedObjectsOrOrphans = true;
                    Log.w("ValidationUtils.checkForOverDeterminedObjects",id + " is overdetermined");
                }
            } else if (checklistObject instanceof ChecklistTask){
                if (triggeredElements.get(id)>1){
                    noOverdeterminedObjectsOrOrphans = true;
                    Log.w("ValidationUtils.checkForOverDeterminedObjects",id + " is overdetermined");
                }

            }
        }
        return noOverdeterminedObjectsOrOrphans;
    }


}
