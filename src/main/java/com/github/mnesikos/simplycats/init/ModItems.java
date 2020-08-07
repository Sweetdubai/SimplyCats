package com.github.mnesikos.simplycats.init;

import com.github.mnesikos.simplycats.Ref;
import com.github.mnesikos.simplycats.SimplyCats;
import com.github.mnesikos.simplycats.item.CatBookItem;
import com.github.mnesikos.simplycats.item.CatnipItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {
    public static final CatBookItem CAT_BOOK = new CatBookItem(new Item.Settings().group(SimplyCats.GROUP));
    public static final CatnipItem CATNIP = new CatnipItem(new Item.Settings().group(SimplyCats.GROUP));

//    public static ItemBase PET_CARRIER = new ItemPetCarrier();
//    public static ItemBase CERTIFICATE = new ItemCertificate("certificate");
//    public static ItemBase TREAT_BAG = new ItemTreatBag("treat_bag");
//    public static ItemCatnipSeeds CATNIP_SEEDS = new ItemCatnipSeeds("catnip_seeds");
//    public static final ItemLaserPointer LASER_POINTER = new ItemLaserPointer("laser_pointer");

    /*public static final Map<EnumDyeColor, ItemCatBowl> CAT_BOWLS = new HashMap<>();
    public static final Map<EnumDyeColor, ItemLitterBox> LITTER_BOXES = new HashMap<>();
    public static final Map<BlockPlanks.EnumType, ItemScratchingPost> SCRATCHING_POSTS = new HashMap<>();*/

    /*public static void registerOres() {
        OreDictionary.registerOre("cropCatnip", CATNIP);
        OreDictionary.registerOre("cropCatmint", CATNIP);
    }*/

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(Ref.MOD_ID, "cat_book"), CAT_BOOK);
//        Registry.register(Registry.ITEM, new Identifier(Ref.MOD_ID, ""), PET_CARRIER);
//        Registry.register(Registry.ITEM, new Identifier(Ref.MOD_ID, "certificate"), CERTIFICATE);
//        Registry.register(Registry.ITEM, new Identifier(Ref.MOD_ID, "treat_bag"), TREAT_BAG);
        Registry.register(Registry.ITEM, new Identifier(Ref.MOD_ID, "catnip"), CATNIP);
//        Registry.register(Registry.ITEM, new Identifier(Ref.MOD_ID, "catnip_seeds"), CATNIP_SEEDS);
//        Registry.register(Registry.ITEM, new Identifier(Ref.MOD_ID, "laser_pointer"), LASER_POINTER);
        /*for (EnumDyeColor color : EnumDyeColor.values()) {
            CAT_BOWLS.put(color, new ItemCatBowl("cat_bowl", color));
            registry.register(CAT_BOWLS.get(color));
            LITTER_BOXES.put(color, new ItemLitterBox("litter_box", color));
            registry.register(LITTER_BOXES.get(color));
        }
        for (BlockPlanks.EnumType type : BlockPlanks.EnumType.values()) {
            SCRATCHING_POSTS.put(type, new ItemScratchingPost("scratching_post", type));
            registry.register(SCRATCHING_POSTS.get(type));
        }*/
    }
}
