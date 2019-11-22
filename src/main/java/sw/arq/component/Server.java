package sw.arq.component;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Server extends Thread {

    private final int port;
    private final int messageLength;

    public Server(int port, int messageLength) {
        this.port = port;
        this.messageLength = messageLength;
    }

    @Override
    public void run() {
        try {
            Socket socket = new ServerSocket(port).accept();
            int index = 0;
            byte[] buffer = new byte[messageLength];
            InputStream is = socket.getInputStream();
            while(index < buffer.length) {
                byte[] pack = new byte[5];
                is.read(pack);
                buffer[index] = pack[4];
                int id = ByteBuffer.wrap(pack, 0, 4).getInt();
                socket.getOutputStream().write(id == 0 ? 1 : 0);
                index++;
            }
            System.out.println("package message -> " + new String(buffer));
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
