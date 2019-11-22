package sw.arq.component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Delivery extends Thread {

    private final byte[] message;
    private final int port;

    public Delivery(int port, byte[] message) {
        this.message = message;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            Object lock = new Object();
            int index = 0;
            int currentId = 0;
            Socket socket = new Socket(InetAddress.getLocalHost(), port);
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            AckThread ackThread = new AckThread(lock, is);
            ackThread.start();
            while (index < message.length) {
                byte[] pack = ByteBuffer.allocate(5).putInt(currentId).put(message[index]).array();
                os.write(pack);
                synchronized (lock) {
                    lock.wait(180000);
                }
                if (ackThread.ack() == currentId) {
                    continue;
                } else {
                    index++;
                    currentId = ackThread.ack() == 0 ? 0 : 1;
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
