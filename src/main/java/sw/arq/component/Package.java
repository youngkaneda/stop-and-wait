package sw.arq.component;

import java.io.Serializable;

@Deprecated
public class Package implements Serializable {

    private int id;
    private byte frame;

    public Package(int id, byte frame) {
        this.id = id;
        this.frame = frame;
    }

    public int id() {
        return id;
    }

    public byte frame() {
        return frame;
    }
}
