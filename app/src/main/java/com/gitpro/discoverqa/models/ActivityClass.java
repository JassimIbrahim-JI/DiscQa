package com.gitpro.discoverqa.models;

import java.io.Serializable;

public class ActivityClass implements Serializable {

    public String nameEN;
    public String nameAR;
    public int cost;
    public double time;

    public ActivityClass(String nameEN, String nameAR, int cost, double time) {
        this.nameEN = nameEN;
        this.nameAR = nameAR;
        this.cost = cost;
        this.time = time;
    }
    public ActivityClass() {

    }



}
