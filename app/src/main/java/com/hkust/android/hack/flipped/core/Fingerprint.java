package com.hkust.android.hack.flipped.core;

import java.util.Arrays;

/**
 * Created by joshua on 14-4-26.
 */
public class Fingerprint {

    public String time;
    public String[] macs;
    public double[] strengths;
    public String location;

    public Fingerprint(String time,String[] macs, double[] strengths) {
        super();
        this.strengths = strengths;
        this.macs = macs;
        this.time = time;
    }

    public String toString(){
        return this.time+'\t'+ Arrays.toString(this.macs) +'\n'+ Arrays.toString(this.strengths);
    }
}
