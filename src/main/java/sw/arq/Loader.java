package sw.arq;

import sw.arq.component.AckThread;
import sw.arq.component.Package;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Hello world!
 *
 */
public class Loader {
    public static void main( String[] args ) {
        //receiver
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new ServerSocket(8666).accept();
                    int index = 0;
                    byte[] buffer = new byte[1024];
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    while(index < buffer.length) {
                        Package pack = (Package) ois.readObject();
                        buffer[index] = pack.frame();
                        if (pack.id() == 2) {
                            break;
                        }
                        socket.getOutputStream().write(pack.id() == 0 ? 1 : 0);
                        index++;
                    }
                    socket.close();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //sender
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Object lock = new Object();
                    int index = 0;
                    int currentId = 0;
                    String message = "i'm small";
                    byte[] bytes = message.getBytes();
                    Socket socket = new Socket(InetAddress.getLocalHost(), 8666);
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    InputStream is = socket.getInputStream();
                    AckThread ackThread = new AckThread(lock, is);
                    ackThread.start();
                    while (index < bytes.length) {
                        oos.writeObject(new Package(currentId, bytes[index]));
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
                    oos.writeObject(new Package(2, (byte) 0));
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
