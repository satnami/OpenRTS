/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.army.effects;

import geometry3D.Point3D;
import java.util.ArrayList;
import math.MyRandom;
import model.battlefield.army.components.Projectile;
import model.battlefield.army.components.Unit;
import model.builders.EffectBuilder;

/**
 *
 * @author Benoît
 */
public class PersistentEffect extends Effect {
    protected final int periodCount;
    protected final ArrayList<Double> durations;
    protected final ArrayList<Double> ranges;
    
    private boolean launched = false;
    public boolean terminated = false;
    
    private int count = 0;
    private int periodIndex = 0;
    private int effectIndex = 0;
    
    private long lastPeriod;
    private double currentPeriodDuration;

    public PersistentEffect(int periodCount,
            ArrayList<Double> durations,
            ArrayList<Double> ranges,
            ArrayList<EffectBuilder> effectBuilders,
            EffectSource source,
            EffectTarget target) {
        super(effectBuilders, source, target);
        this.periodCount = periodCount;
        this.durations = durations;
        this.ranges = ranges;
    }

    @Override
    public void launch(){
        launched = true;
        setNextPeriodDuration();
        lastPeriod = System.currentTimeMillis();
    }
    
    public void update(){
        if(!launched)
            return;
        if(!source.isStillActiveSource())
            terminated = true;
        if(!terminated && lastPeriod+currentPeriodDuration < System.currentTimeMillis()){
            childEffectBuilders.get(effectIndex).build(source, target, null).launch();
            
            if(++effectIndex >= childEffectBuilders.size())
                effectIndex = 0;
            if(++periodIndex >= durations.size())
                periodIndex = 0;
            setNextPeriodDuration();
            lastPeriod = System.currentTimeMillis();

            if(++count >= periodCount)
                terminated = true;
        }
    }
    
    private void setNextPeriodDuration(){
        currentPeriodDuration = durations.get(periodIndex)+ MyRandom.between(0, ranges.get(periodIndex));
    }
    
}