package com.github.mnesikos.simplycats.item;

import com.github.mnesikos.simplycats.entity.EntityCat;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCertificate extends Item {
    private final boolean isAdoptCertificate;

    public ItemCertificate(boolean adoptCertificate) {
        super();
        this.isAdoptCertificate = adoptCertificate;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        if (target instanceof EntityCat || target instanceof EntityWolf || target instanceof EntityParrot) {
            EntityTameable tameable = (EntityTameable) target;
            if (this.isAdoptCertificate) {
                if ((tameable instanceof EntityCat && ((EntityCat) tameable).canBeTamed(player)) || (!(tameable instanceof EntityCat) && !tameable.isTamed())) {
                    if (tameable instanceof EntityCat)
                        ((EntityCat) tameable).setTamed(true, player);
                    else
                        tameable.setTamed(true);
                    tameable.getNavigator().clearPath();
                    tameable.setOwnerId(player.getUniqueID());
                    tameable.setHealth(tameable.getMaxHealth());
                    player.sendStatusMessage(new TextComponentTranslation("chat.info.adopt_usage", tameable.getName()), true);
                    if (player.world.isRemote)
                        this.playTameEffect(true, tameable.world, tameable);

                    if (!player.capabilities.isCreativeMode) {
                        stack.shrink(1);
                        if (stack.getCount() <= 0)
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
                    }

                } else if (tameable instanceof EntityCat && !tameable.isTamed())
                    player.sendStatusMessage(new TextComponentTranslation("chat.info.tamed_limit_reached"), true);

            } else {
                if (tameable.isOwner(player)) {
                    if (tameable instanceof EntityCat)
                        ((EntityCat) tameable).setTamed(false, player);
                    else
                        tameable.setTamed(false);
                    tameable.getNavigator().clearPath();
                    tameable.setOwnerId(null);
                    player.sendStatusMessage(new TextComponentTranslation("chat.info.release_usage", tameable.getName()), true);
                    this.playTameEffect(false, tameable.world, tameable);

                    if (!player.capabilities.isCreativeMode) {
                        stack.shrink(1);
                        if (stack.getCount() <= 0)
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
                    }
                }
                return true;
            }
        }
        return false;
    }

    protected void playTameEffect(boolean play, World world, EntityTameable entity) {
        EnumParticleTypes enumparticletypes = EnumParticleTypes.HEART;

        if (!play)
            enumparticletypes = EnumParticleTypes.SMOKE_NORMAL;

        for (int i = 0; i < 7; ++i) {
            double d0 = world.rand.nextGaussian() * 0.02D;
            double d1 = world.rand.nextGaussian() * 0.02D;
            double d2 = world.rand.nextGaussian() * 0.02D;
            world.spawnParticle(enumparticletypes, entity.posX + (double) (world.rand.nextFloat() * entity.width * 2.0F) - (double) entity.width, entity.posY + 0.5D + (double) (world.rand.nextFloat() * entity.height), entity.posZ + (double) (world.rand.nextFloat() * entity.width * 2.0F) - (double) entity.width, d0, d1, d2);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("tooltip.certificate_" + (this.isAdoptCertificate ? "adopt" : "release") + ".desc"));
    }
}
