package server;

import util.ClosableThread;

import java.util.ArrayList;
import java.util.Arrays;

public class Shutdown extends Thread {

    private ArrayList<ClosableThread> closableThreads = new ArrayList<>();

    public Shutdown(ClosableThread... closableThreads) {
        this.closableThreads.addAll(Arrays.asList(closableThreads));
    }

    void addClosableThreads(ClosableThread... closableThreads) {
        this.closableThreads.addAll(Arrays.asList(closableThreads));
    }

    @Override
    public void run() {
        System.out.println("Received shutdown exiting...");
        for (ClosableThread clean: closableThreads) {
            clean.close();
        }
    }
}
