package com;
/**
 * This class is used to ensure that the dropping speed of blocks is not influence by other factor
 * and always remain constant
 */
public class Clock {
    private float milliPerCycle;
    public long lastUpdate;
    boolean isPaused;
    public int elapsedCycle;
    private float excessCycle;
    private Tetris tetris;
    public Clock(Tetris tetris) {
        this.tetris = tetris;
        reset();
    }
    public void reset() {
        this.isPaused = false;
        this.lastUpdate = getCurrentTime();
        this.excessCycle = 0.0f;
        this.elapsedCycle = 0;
    }
    public void setCyclePerSecond(float cyclePerSecond) {
        this.milliPerCycle = ((1.0f)  * 1000L / cyclePerSecond) ;
    }
    /**
     * convert nanosecond to millisecond
     */
    public long getCurrentTime() {
        return (System.nanoTime() / 1000000L);
    }
    /**
     * Calculate the basic variable that used in controling refreshing rate
     * The excessCycle is the remainder that divided by milliPerCycle,it's used in next time update() called,to ensure
     * that time is fully used
     * excessCycle is used to control refresh rate
     */
    public void update() {
        long currentUpdate = getCurrentTime();
        float delta = (float)(currentUpdate - lastUpdate) + excessCycle;

        if (!isPaused) {
            this.elapsedCycle = (int)Math.floor(delta / milliPerCycle);
            this.excessCycle = delta % milliPerCycle;
        }
        this.lastUpdate = currentUpdate;
    }
    public void setPaused(boolean pause) {
        this.isPaused = pause;
    }
    public boolean isPaused() {
        return isPaused;
    }
    public boolean hasElapsedCycle() {
        if (elapsedCycle > 0) {
            this.elapsedCycle--;
            return true;
        }

        return false;
    }
    public boolean peekElapsedCycle() {
        return (elapsedCycle > 0);
    }
}