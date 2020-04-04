package com.enigmadux.craterguardians.Animations;

public abstract class FrameTransitionAnim extends TransitionAnim {
    private static final long DELAY_MILLIS = 16;

    private long startMillis;
    protected long millisLeft;
    protected long totalMillis;
    protected long finishedMillis;

    private boolean cancel;
    FrameTransitionAnim(long millis){
        startMillis = System.currentTimeMillis();
        finishedMillis = 0;
        totalMillis = millisLeft= millis;

    }
    void start(){
        HANDLER.postDelayed(this,0);
    }
    void start(long delay){
        HANDLER.postDelayed(this,delay);
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
        millisLeft = startMillis + totalMillis - System.currentTimeMillis();
        finishedMillis = System.currentTimeMillis() - startMillis;

        HANDLER.postDelayed(this,DELAY_MILLIS);

    }

    abstract void step();

    void finish(){

    }
    public void cancel(){
        this.cancel = true;
    }

    public long getMillisLeft(){
        return millisLeft;
    }
}
