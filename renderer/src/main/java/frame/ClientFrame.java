package frame;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;

public class ClientFrame extends JFrame {

    private final int width;

    private final int height;

    public ClientFrame(int width, int height, String title) throws HeadlessException {
        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.setMinimumSize(this.getSize());
        this.setTitle(title);
        this.setVisible(true);
    }

    public void renderGame(Applet client) {
        client.setSize(width, height);
        client.init();
        client.start();
        this.setContentPane(client);
    }
}
