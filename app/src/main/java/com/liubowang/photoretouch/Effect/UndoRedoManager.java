package com.liubowang.photoretouch.Effect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heshaobo on 2017/12/1.
 */

public class UndoRedoManager {


    private List<UndoRedoInfo> actioinList = new ArrayList<>();
    private int historyIndex = -1;
    public void addAction(UndoRedoInfo info){
        if (historyIndex >= -1 &&
                historyIndex < actioinList.size() - 1) {
            actioinList = actioinList.subList(0, historyIndex + 1);
        }
        actioinList.add(info);
        historyIndex ++;
    }



    public UndoRedoInfo undo() {
        if (historyIndex > -1 &&
                actioinList.size() > 0) {
            UndoRedoInfo info = actioinList.get(historyIndex);
            historyIndex--;
            return info;
        }
        return null;
    }

    public boolean canUndo() {
        return historyIndex > -1 &&
                actioinList.size() > 0;
    }
    public UndoRedoInfo redo() {
        if (historyIndex <= actioinList.size() - 1) {
            historyIndex++;
            UndoRedoInfo info = actioinList.get(historyIndex);
            return info;
        }
        return null;
    }
    public boolean canRedo() {
        return historyIndex < actioinList.size() - 1;
    }

    public void clean(){
        actioinList.clear();
        historyIndex = -1;
    }

    public UndoRedoInfo getLastInfo(){
        return actioinList.get(actioinList.size()-1);
    }




}
