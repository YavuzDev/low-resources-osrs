package com.bot.mixin;

import com.bot.api.Client;
import com.bot.api.GameEngine;
import com.bot.inject.mixin.Getter;
import com.bot.inject.mixin.Mixin;
import com.bot.inject.mixin.Setter;
import com.bot.inject.mixin.Shadow;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.URL;

@Mixin("GameEngine")
public abstract class GameEngineMixin implements GameEngine {

    @Shadow
    private static Client client;

    @Getter
    @Setter
    private AppletStub stub;

    public String getParameter(String key) {
        return stub.getParameter(key);
    }

    public URL getDocumentBase() {
        return stub.getDocumentBase();
    }

    public URL getCodeBase() {
        return stub.getCodeBase();
    }

    public AppletContext getAppletContext() {
        return stub.getAppletContext();
    }

}
