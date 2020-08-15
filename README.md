# Low Resources OSRS Client

An attempt at an Oldschool Runescape client that doesn't use a lot of resources.

The project contains 3 modules, a hooks finder, a hooks injector and a renderer.

# Hooks Finder

The hooks finder module is used to find: classes, static fields, static methods, fields and methods based on some conditions.

To find them based on conditions, ASM is used to read the obfuscated classes from the OSRS client jar.

All the hooks are loaded using reflection based on the package they are in(`com.osrs.visitor.impl`).

## Example Visitors

### ClientVisitor

```java
package com.osrs.visitor.impl;

import com.osrs.visitor.HookVisitor;
import com.osrs.visitor.VisitorInfo;
import com.osrs.visitor.condition.Condition;
import org.objectweb.asm.Opcodes;

import java.util.List;

@VisitorInfo(name = "Client", dependsOn = {WidgetVisitor.class})
public class ClientVisitor extends HookVisitor {

    @Override
    public List<Condition> conditions() {
        return List.of(fieldCondition(5, "Widget"));
    }

    @Override
    public void onSetClassNode() {
        var method = getMethod(parameterCondition("Widget"), parameterCondition(2, "Int"));
        var viewport = getField(method, opcodeCondition(Opcodes.PUTSTATIC), fieldCondition("Widget"));

        addStaticFieldHook("viewport", viewport);
    }
}
```

The VisitorInfo annotation contains the deobfuscated name and what other visitors it depends on.
As you can see ClientVisitor depends on WidgetVisitor, we need this since the obfuscated class contains 5 fields of the type Widget.

The List<Condition> conditions() function is used to grab the correct obfuscated class based on some conditions.

### WidgetVisitor

```java
package com.osrs.visitor.impl;

import com.osrs.hook.local.FieldHook;
import com.osrs.visitor.HookVisitor;
import com.osrs.visitor.VisitorInfo;
import com.osrs.visitor.condition.Condition;

import java.util.List;

@VisitorInfo(name = "Widget")
public class WidgetVisitor extends HookVisitor {

    @Override
    public List<Condition> conditions() {
        return List.of(fieldCondition(50, 80, "Int"), fieldCondition(6, "String"));
    }

    @Override
    public void onSetClassNode() {
        var hidden = getFieldsFromCount(callCountCondition(7, "Boolean"));
        for (int i = 0; i < hidden.size(); i++) {
            addFieldHook("hidden" + i, new FieldHook(hidden.get(i).getName(), hidden.get(i).getType()));
        }
    }
}

```

### Generated Hooks (8/15/2020)

These are all the hooks that get found by all the visitors in `com.osrs.visitor.impl`

```json
{
  "statics": {
    "fields": {
      "viewport": {
        "owner": "client",
        "name": "nk",
        "type": "Lhd;"
      },
      "client": {
        "owner": "ae",
        "name": "au",
        "type": "Lclient;"
      }
    },
    "methods": {
      "updateNpcs": {
        "owner": "q",
        "name": "hi",
        "type": "(ZLkb;I)V",
        "dummyValue": 1542604247
      }
    }
  },
  "classes": {
    "AE": {
      "name": "ae",
      "fields": {},
      "methods": {}
    },
    "LongVarSerializer": {
      "name": "q",
      "fields": {},
      "methods": {}
    },
    "Widget": {
      "name": "hd",
      "fields": {
        "hidden1": {
          "name": "v",
          "type": "Z"
        },
        "hidden0": {
          "name": "az",
          "type": "Z"
        },
        "hidden2": {
          "name": "ap",
          "type": "Z"
        }
      },
      "methods": {}
    },
    "GameEngine": {
      "name": "bn",
      "fields": {},
      "methods": {
        "post": {
          "name": "h",
          "type": "(B)V"
        }
      }
    },
    "Client": {
      "name": "client",
      "fields": {},
      "methods": {}
    },
    "PacketBuffer": {
      "name": "kb",
      "fields": {},
      "methods": {}
    }
  }
}
```

# Hooks Injector

The hooks injector, injects code based on the generated hooks from the Hooks Finder.

The code gets directly injected into the downloaded OSRS jar client.

The injecting code is from: https://github.com/dennisdev/osfx

## Example

```java
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
```

This ClientMixin gets the viewport Widget, copies the updateNpcs function and replaces it.
By setting the viewport to hidden, the rendering gets disabled.

# Renderer

The renderer does nothing special, it just puts the injected jar from the Hooks Injector which is an applet into a JFrame.

## Preview

![Alt Text](https://i.imgur.com/4ayQzfr.gif)


