package com.github.mnesikos.simplycats.item;

import com.github.mnesikos.simplycats.Ref;
import com.github.mnesikos.simplycats.SimplyCats;
import net.minecraft.item.Item;

public class ItemBase extends Item {
    protected String name;

    public ItemBase(String name) {
        this.name = name;
        this.setTranslationKey(name);
        this.setRegistryName(Ref.MODID + ":" + name);
        setCreativeTab(SimplyCats.PROXY.SIMPLYCATS);
    }

    public void registerItemModel() {
        SimplyCats.PROXY.registerItemRenderer(this, 0, name);
    }
}
