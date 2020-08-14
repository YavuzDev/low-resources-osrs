package com.bot.mixin;

import com.bot.api.Client;
import com.bot.api.PacketBuffer;
import com.bot.api.Widget;
import com.bot.inject.mixin.*;

@Mixin("Client")
public abstract class ClientMixin implements Client {

    @Shadow
    private static Client client;

    @Static
    @Getter("viewport")
    @Override
    public abstract Widget getViewportWidget();

    @Copy("updateNpcs")
    private static void rs$UpdateNpcs(boolean var0, PacketBuffer var1, int var2) {
        throw new UnsupportedOperationException();
    }

    @Replace("updateNpcs")
    public static void updateNpcs(boolean var0, PacketBuffer var1, int var2) {
        if (client.getViewportWidget() != null) {
            client.getViewportWidget().setHidden1(true);
            client.getViewportWidget().setHidden2(true);
            client.getViewportWidget().setHidden3(true);
        }
        rs$UpdateNpcs(var0, var1, var2);
    }

}
