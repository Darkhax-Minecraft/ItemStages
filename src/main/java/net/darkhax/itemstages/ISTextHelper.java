package net.darkhax.itemstages;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Collection;
import java.util.Iterator;

public class ISTextHelper {

    public static MutableComponent join (Component separator, Collection<Component> toJoin) {

        return join(separator, toJoin.iterator());
    }

    public static MutableComponent join (Component separator, Iterator<Component> toJoin) {

        final MutableComponent joined = Component.literal("");

        while (toJoin.hasNext()) {

            joined.append(toJoin.next());

            if (toJoin.hasNext()) {

                joined.append(separator);
            }
        }

        return joined;
    }
}
