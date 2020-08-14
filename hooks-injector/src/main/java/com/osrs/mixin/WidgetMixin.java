package com.osrs.mixin;

import com.osrs.api.Widget;
import com.osrs.inject.mixin.Mixin;
import com.osrs.inject.mixin.Setter;

@Mixin("Widget")
public abstract class WidgetMixin implements Widget {

    @Override
    @Setter("hidden0")
    public abstract void setHidden1(boolean hidden1);

    @Override
    @Setter("hidden1")
    public abstract void setHidden2(boolean hidden2);

    @Override
    @Setter("hidden2")
    public abstract void setHidden3(boolean hidden3);
    
}
