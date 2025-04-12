package com.simulation.multiprocessing;

public class Solver {
    Thread thread;
    Runnable target;
    public boolean active = false;
    boolean need_activation;
    int tmp = 0;

    public Solver(Runnable target, boolean activation){
        this.target = target;
        need_activation = activation;
        if (!need_activation) active = true;
        thread = new Thread(() -> {
            while (true){
                if (!active){
                    System.out.print("");
                    continue;
                }
                this.target.run();
                if (this.need_activation) active = false;
                tmp = 0;
            }
        });
        thread.setDaemon(true);
    }

    public void start(){
        thread.start();
    }

    public void terminate(){
        thread.stop();
    }

    public void trigger(){
        active = true;
    }
}
