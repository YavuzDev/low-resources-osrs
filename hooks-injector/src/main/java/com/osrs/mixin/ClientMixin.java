package com.osrs.mixin;

import com.osrs.api.Client;
import com.osrs.api.PacketBuffer;
import com.osrs.api.Widget;
import com.osrs.inject.mixin.*;

@Mixin("Client")
public abstract class ClientMixin implements Client {

    @Shadow
    private static Client client;

    @Static
    @Getter("viewport")
    @Override
    public abstract Widget getViewportWidget();

    @Copy("updateNpcs")
    private static void rs$UpdateNpcs(boolean var0, PacketBuffer var1) {
        throw new UnsupportedOperationException();
    }

    @Replace("updateNpcs")
    public static void updateNpcs(boolean var0, PacketBuffer var1) {
        var viewport = client.getViewportWidget();
        if (viewport != null) {
            viewport.setHidden1(true);
            viewport.setHidden2(true);
            viewport.setHidden3(true);
        }
        rs$UpdateNpcs(var0, var1);
    }

}
