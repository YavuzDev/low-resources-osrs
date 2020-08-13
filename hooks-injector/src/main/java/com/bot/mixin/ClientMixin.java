package com.bot.mixin;

import com.bot.api.Client;
import com.bot.api.Widget;
import com.bot.inject.mixin.Getter;
import com.bot.inject.mixin.Mixin;
import com.bot.inject.mixin.Static;

@Mixin("Client")
public abstract class ClientMixin implements Client {

    @Static
    @Getter("viewport")
    @Override
    public abstract Widget getViewportWidget();

}
