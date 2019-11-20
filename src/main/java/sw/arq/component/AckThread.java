package sw.arq.component;

import java.io.IOException;
import java.io.InputStream;

public class AckThread extends Thread {

    private Object lock;
    private int ack;
    private InputStream is;

    public AckThread(Object lock, InputStream is) {
        this.lock = lock;
        this.is = is;
        this.ack = 0;
    }

    @Override
    public void run() {
        try {
            int read;
            while ((read = is.read()) != -1) {
                this.ack = read;
                synchronized (lock) {
                    lock.notify();
                }
            }
            this.is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int ack() {
        return ack;
    }
}
