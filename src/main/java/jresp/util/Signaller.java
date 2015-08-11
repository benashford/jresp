package jresp.util;

public class Signaller {
    private int count = 0;

    public synchronized void signal() {
        count++;
        notifyAll();
    }

    public synchronized void reset() {
        if (count > 0) {
            count = 0;
        } else {
            try {
                wait();
            } catch (InterruptedException e) {
                // harmless
            }
        }
    }
}
