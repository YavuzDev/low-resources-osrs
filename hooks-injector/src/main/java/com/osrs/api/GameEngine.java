package com.osrs.api;

import java.applet.AppletStub;
import java.awt.event.FocusListener;
import java.awt.event.WindowListener;

public interface GameEngine extends Runnable, FocusListener, WindowListener {

    void init();

    void start();

    void stop();

    void destroy();

    void setStub(AppletStub stub);

    void setSize(int width, int height);

}
