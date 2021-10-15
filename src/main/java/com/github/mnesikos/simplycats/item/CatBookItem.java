package com.github.mnesikos.simplycats.item;

import com.github.mnesikos.simplycats.SimplyCats;
import com.github.mnesikos.simplycats.client.gui.CatBookScreen;
import com.github.mnesikos.simplycats.entity.SimplyCatEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;

public class CatBookItem extends Item {
    public static final int GUI_ID = 1;

    public CatBookItem() {
        super(new Item.Properties().tab(SimplyCats.ITEM_GROUP).stacksTo(1));
    }

    public void addBookmarkedPage(int id) {
        // todo
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
        if (target instanceof SimplyCatEntity) {
            SimplyCatEntity cat = (SimplyCatEntity) target;
            stack = player.getItemInHand(hand);

            CompoundNBT compound = stack.getOrCreateTag();

            ListNBT tagList;
            boolean catExists = false;
            int catInList = 0;
            if (compound.contains("pages")) {
                tagList = compound.getList("pages", Constants.NBT.TAG_COMPOUND);
                for (int i = 0; i < tagList.size(); ++i) {
                    if (tagList.getCompound(i).getUUID("UUID").equals(cat.getUUID())) {
                        catExists = true;
                        catInList = i;
                        break;
                    }
                }
            } else {
                tagList = new ListNBT();
                compound.put("pages", tagList);
            }

            if (!catExists) {
                CompoundNBT catTag = new CompoundNBT();
                cat.saveWithoutId(catTag);
                tagList.add(catTag);
            }

            stack.setTag(compound);

            if (player.isDiscrete()) { // todo
                if (player.level.isClientSide) {
                    CatBookScreen.book = stack;
                    Minecraft.getInstance().setScreen(new CatBookScreen(cat, catInList));
                }
            }

        }
        return super.interactLivingEntity(stack, player, target, hand);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) { // todo
        ItemStack book = player.getItemInHand(hand);
        if (!player.isDiscrete()) {
            if (world.isClientSide) {
                CatBookScreen.book = book;
                Minecraft.getInstance().setScreen(new CatBookScreen());
            }
        }

        return super.use(world, player, hand);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("tooltip.cat_book.usage"));
    }
}
