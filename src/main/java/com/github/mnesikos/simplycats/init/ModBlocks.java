package com.github.mnesikos.simplycats.init;

import com.github.mnesikos.simplycats.Ref;
import com.github.mnesikos.simplycats.SimplyCats;
import com.github.mnesikos.simplycats.block.ScratchingPostBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlocks {
    public static final ScratchingPostBlock OAK_SCRATCHING_POST = new ScratchingPostBlock(FabricBlockSettings.of(Material.WOOD));
    public static final ScratchingPostBlock SPRUCE_SCRATCHING_POST = new ScratchingPostBlock(FabricBlockSettings.of(Material.WOOD));
    public static final ScratchingPostBlock BIRCH_SCRATCHING_POST = new ScratchingPostBlock(FabricBlockSettings.of(Material.WOOD));
    public static final ScratchingPostBlock JUNGLE_SCRATCHING_POST = new ScratchingPostBlock(FabricBlockSettings.of(Material.WOOD));
    public static final ScratchingPostBlock ACACIA_SCRATCHING_POST = new ScratchingPostBlock(FabricBlockSettings.of(Material.WOOD));
    public static final ScratchingPostBlock DARK_OAK_SCRATCHING_POST = new ScratchingPostBlock(FabricBlockSettings.of(Material.WOOD));

    public static void register() {
        register(OAK_SCRATCHING_POST, "oak_scratching_post");
        register(SPRUCE_SCRATCHING_POST, "spruce_scratching_post");
        register(BIRCH_SCRATCHING_POST, "birch_scratching_post");
        register(JUNGLE_SCRATCHING_POST, "jungle_scratching_post");
        register(ACACIA_SCRATCHING_POST, "acacia_scratching_post");
        register(DARK_OAK_SCRATCHING_POST, "dark_oak_scratching_post");
    }

    private static void register(Block block, String id) {
        Registry.register(Registry.BLOCK, new Identifier(Ref.MOD_ID, id), block);
        Registry.register(Registry.ITEM, new Identifier(Ref.MOD_ID, id), new BlockItem(block, new Item.Settings().group(SimplyCats.GROUP)));
    }

//    public static BlockCropCatnip CROP_CATNIP = new BlockCropCatnip("crop_catnip");
//    public static BlockCatBowl CAT_BOWL = new BlockCatBowl("cat_bowl");
//    public static BlockLitterBox LITTER_BOX = new BlockLitterBox("litter_box");
//    public static BlockScratchingPost SCRATCHING_POST = new BlockScratchingPost("scratching_post");

    /*public static void register(IForgeRegistry<Block> registry) {
        registry.registerAll(
                CROP_CATNIP,
                CAT_BOWL,
                LITTER_BOX,
                SCRATCHING_POST
        );

        GameRegistry.registerTileEntity(CAT_BOWL.getTileEntityClass(), Objects.requireNonNull(CAT_BOWL.getRegistryName()));
        GameRegistry.registerTileEntity(LITTER_BOX.getTileEntityClass(), Objects.requireNonNull(LITTER_BOX.getRegistryName()));
    }*/

    /*public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.registerAll(
                //LITTER_BOX.createItemBlock()
        );
    }*/

    /*public static void registerModels() {
        //LITTER_BOX.registerItemModel(Item.getItemFromBlock(LITTER_BOX));
    }*/
}
