package com.pressstarttolive.logger.todoornottodo;

import android.content.Context;
import android.graphics.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by logger on 12/20/14.
 */
public class ScriptParser {
    private static final String DEFAULT_SCRIPT_NAME = "demo-script.txt";

    private static class Flags {
        static final String SAVE_PRUNED = "savepruned";
        static final String SAVE_COMPLETED = "savecompleted";
        static final String DEFAULT_COLOR = "defaultcolor";
    }

    private static class ChecklistMarkers {
        static final String TASK = "task";
        static final String BLOCK = "block";
        static final String TITLE = "title";
        static final String DESCRIPTION = "desc";
        static final String TRIGGER = "trigger";
        static final String PRUNE = "prune";
        static final String COUNT = "count";
        static final String CHECKBOX = "checkbox";
        static final String COLOR = "color";
    }

    public static Script parseDefaultScript(Context context){
        return parseScript(context, DEFAULT_SCRIPT_NAME);
    }

    public static Script parseScript(Context context, String fileName){
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            return parseScript(new BufferedReader(new InputStreamReader(inputStream)));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static Script parseScript(BufferedReader input){
        try {
            Script script = new Script();
            String firstObjectsLine = parseFlags(input,script);
            parseChecklistObjects(input,script,firstObjectsLine);
            return script;
        } catch (ScriptException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static void parseChecklistObjects(BufferedReader input, Script script, String firstLine) throws ScriptException, IOException {
        ChecklistObject checklistObject=null;
        for (String line = firstLine;line!=null;line=input.readLine()){
            if (isParsedLine(line)){
                String[] splitLine = line.split(":",2);
                String property = splitLine[0].toLowerCase();
                String value = splitLine[1];
                switch (property){
                    case ChecklistMarkers.TASK:
                        if (checklistObject!=null){
                            script.addChecklistObject(checklistObject);
                        }
                        else {
                            script.setOrigin(value);
                        }
                        checklistObject = new ChecklistTask(value);
                        break;
                    case ChecklistMarkers.BLOCK:
                        if (checklistObject!=null){
                            script.addChecklistObject(checklistObject);
                        }
                        else {
                            script.setOrigin(value);
                        }
                        checklistObject = new ChecklistBlock(value);
                        break;
                    case ChecklistMarkers.TITLE:
                        if (!(checklistObject instanceof ChecklistTask)){
                            throw new ScriptException("Malformatted script line: Only tasks can have titles "+line);
                        }
                        ((ChecklistTask) checklistObject).setTitle(value);
                        break;
                    case ChecklistMarkers.DESCRIPTION:
                        if (!(checklistObject instanceof ChecklistTask)){
                            throw new ScriptException("Malformatted script line: Only tasks can have descriptions "+line);
                        }
                        ((ChecklistTask) checklistObject).setDescription(value);
                        break;
                    case ChecklistMarkers.TRIGGER:
                        checklistObject.addChildren(value.split(","));
                        break;
                    case ChecklistMarkers.PRUNE:
                        checklistObject.addRivals(value.split(","));
                        break;
                    case ChecklistMarkers.COUNT:
                        if (!(checklistObject instanceof ChecklistBlock)){
                            throw new ScriptException("Malformatted script line: Only blocks can have counts "+line);
                        }
                        ((ChecklistBlock) checklistObject).setCount(Integer.decode(value));
                        break;
                    case ChecklistMarkers.CHECKBOX:
                        if (!(checklistObject instanceof ChecklistTask)){
                            throw new ScriptException("Malformatted script line: Only tasks can have checkbox "+line);
                        }
                        ((ChecklistTask) checklistObject).setHasCheckbox(readBoolean(value));
                        break;
                    case ChecklistMarkers.COLOR:
                        if (!(checklistObject instanceof ChecklistTask)){
                            throw new ScriptException("Malformatted script line: Only tasks can have color "+line);
                        }
                        ((ChecklistTask) checklistObject).setColor(Color.parseColor(value));
                        break;
                    default:
                        throw new ScriptException("Malformatted script line: "+line);
                }
            }
        }
        if (checklistObject!=null) {
            script.addChecklistObject(checklistObject);
        } else {
            throw new ScriptException("Empty script");
        }
    }

    private static String parseFlags(BufferedReader input, Script script) throws IOException, ScriptException {
        for (String line = input.readLine();line!=null;line=input.readLine()){
            if (isParsedLine(line)){
                String[] splitLine = line.split(":",2);
                String property = splitLine[0].toLowerCase();
                String value = splitLine[1].toLowerCase();
                switch (property){
                    case Flags.SAVE_COMPLETED:
                        script.saveCompleted = readBoolean(value);
                        break;
                    case Flags.SAVE_PRUNED:
                        script.savePruned = readBoolean(value);
                        break;
                    case ChecklistMarkers.TASK:
                    case ChecklistMarkers.BLOCK:
                        return line;
                    default:
                        throw new ScriptException("Malformatted script line: "+line);
                }
            }
        }
        throw new ScriptException("Empty script");
    }

    private static Boolean readBoolean(String bool) throws ScriptException {
        if (bool.toLowerCase().equals("true")){
            return true;
        } else if (bool.toLowerCase().equals("false")) {
            return false;
        } else {
            throw new ScriptException("Malformatted script line, boolean value must be true or false");
        }
    }

    private static boolean isParsedLine(String line){
        return line.length()>0
                && !( (line.length()>=1 && line.substring(0,1).equals("#")))
                && !( (line.length()>=2 && line.substring(0,2).equals("//")));
    }

    private static class ScriptException extends Exception{
        ScriptException(String cause){
            super(cause);
        }
    }
}

