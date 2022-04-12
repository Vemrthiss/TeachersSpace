package com.teachersspace.schedule;

import java.util.ArrayList;
import java.util.Map;

public class SlotsModel {
    private ArrayList<Map> slots;

    SlotsModel(){
        ;
    }

    private SlotsModel(ArrayList<Map> slots){
        this.slots=slots;
    }

    public ArrayList<Map> getSlots() {
        return slots;
    }

    public void setSlots(ArrayList<Map> slots) {
        this.slots = slots;
    }


    public Object[] addo(Object[] arr, Object x){
        int i;
        int n= arr.length;
        Object[] narr= new Object[n+1];

        for (i=0; i<n; i++){
            narr[i]= arr[i];
        }
        narr[n]=x;
        return narr;
    }

    public Object[][] addo(Object[][] twodarr, Object[] x){
        int i;
        int n= twodarr.length;
        Object[][] narr= new Object[n+1][3];

        for (i=0; i<n; i++){
            narr[i]= twodarr[i];
        }
        narr[n]=x;
        return narr;
    }
}
