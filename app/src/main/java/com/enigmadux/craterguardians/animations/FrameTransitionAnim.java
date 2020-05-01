package com.enigmadux.craterguardians.animations;

public abstract class FrameTransitionAnim extends TransitionAnim {
    static final long DELAY_MILLIS = 1000L/120L;

    protected long millisLeft;
    protected long totalMillis;
    protected long finishedMillis;

    private boolean cancel;
    private boolean finished;

    private long deltaTime;
    boolean inGameAnim = false;
    FrameTransitionAnim(long millis){
        finishedMillis = 0;
        totalMillis = millisLeft= millis;

    }
    void start(){
        if (inGameAnim) {
            GAME_HANDLER.postDelayed(this, 0);
        } else {
            HANDLER.postDelayed(this,0);
        }
    }
    void start(long delay){
        if (inGameAnim) {
            GAME_HANDLER.postDelayed(this, delay);
        } else {
            HANDLER.postDelayed(this,delay);
        }
    }

    @Override
    public void run() {
        if (cancel){
            return;
        }
        if (millisLeft < 0){
            millisLeft = 0;
            finish();
            return;
        }
        step();
        //not using current time because that doesnt work well with pausing
        finishedMillis += deltaTime;
        millisLeft -= deltaTime;
//        millisLeft = startMillis + totalMillis - System.currentTimeMillis();
//        finishedMillis = System.currentTimeMillis() - startMillis;

        if (inGameAnim){
            GAME_HANDLER.postDelayed(this,DELAY_MILLIS);
        } else {
            HANDLER.postDelayed(this, DELAY_MILLIS);
        }

    }

    public void setDeltaTime(long deltaTime){
        this.deltaTime = deltaTime;
    }

    abstract void step();

    void finish(){
        this.finished = true;
    }
    public void cancel(){
        this.cancel = true;
    }

    public long getMillisLeft(){
        return millisLeft;
    }

    public boolean isFinished() {
        return finished;
    }
}
