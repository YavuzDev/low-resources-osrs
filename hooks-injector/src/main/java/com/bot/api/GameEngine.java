package com.bot.api;

import java.awt.event.FocusListener;
import java.awt.event.WindowListener;

public interface GameEngine extends Runnable, FocusListener, WindowListener {

    void init();

    void start();

    void stop();

    void destroy();

}
