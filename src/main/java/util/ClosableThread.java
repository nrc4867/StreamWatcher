package util;

public abstract class ClosableThread extends Thread{
    /**
     * used for closing and cleanup at program close
     */
    public abstract void close();
}
