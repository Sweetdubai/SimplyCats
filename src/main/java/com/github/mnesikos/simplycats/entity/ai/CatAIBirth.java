package com.github.mnesikos.simplycats.entity.ai;

import com.github.mnesikos.simplycats.configuration.SimplyCatsConfig;
import com.github.mnesikos.simplycats.entity.EntityCat;
import com.github.mnesikos.simplycats.entity.core.Genetics;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.Random;

public class CatAIBirth extends EntityAIBase {

    private final EntityCat mother;
    private EntityCat father;
    World world;
    //int KITTEN_DELAY; // might include later, thinking one kitten spawns at a time

    public CatAIBirth(EntityCat entityCat) {
        this.mother = entityCat;
        this.world = entityCat.world;
        this.setMutexBits(8);
    }

    @Override
    public boolean shouldExecute() {
        if (this.mother.getSex() != Genetics.Sex.FEMALE || !this.mother.getBreedingStatus(EntityCat.BreedingStatus.PREGNANT) || this.mother.getBreedingStatus(EntityCat.BreedingStatus.IN_HEAT)) {
            return false;
        } else return this.mother.getMateTimer() < 0;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.mother.getBreedingStatus(EntityCat.BreedingStatus.PREGNANT);
    }

    @Override
    public void resetTask() {
        this.father = null;
        //KITTEN_DELAY = 0;
    }

    @Override
    public void updateTask() {
        /*++KITTEN_DELAY;
        if (KITTEN_DELAY >= 60)*/

        for (int i = 0; i < this.mother.getKittens(); i++) {
            this.father = new EntityCat(this.world); // create the father cat for kitten referencing
            father.readFromNBT(this.mother.getFather(i)); // set the saved father nbt data to new FATHER cat

            this.spawnBaby(this.father);
            this.mother.getEntityData().removeTag("Father" + i); // deletes just used father data
        }

        this.mother.setKittens(0); // resets kitten counter
        this.mother.setBreedingStatus(EntityCat.BreedingStatus.PREGNANT, false); // ends pregnancy
        this.mother.setTimeCycle(EntityCat.TimeCycle.END, SimplyCatsConfig.HEAT_COOLDOWN); // sets out of heat timer
    }

    private void spawnBaby(EntityCat father) {
        EntityCat child = this.mother.createChild(father);

        final net.minecraftforge.event.entity.living.BabyEntitySpawnEvent event = new net.minecraftforge.event.entity.living.BabyEntitySpawnEvent(mother, father, child);
        child = (EntityCat) event.getChild();

        if (child != null) {
            child.setGrowingAge(-SimplyCatsConfig.KITTEN_MATURE_TIMER);
            child.setLocationAndAngles(this.mother.posX, this.mother.posY, this.mother.posZ, 0.0F, 0.0F);
            child.setParent(EntityCat.FATHER, this.father.getCustomNameTag());
            child.setParent(EntityCat.MOTHER, this.mother.getCustomNameTag());
            this.world.spawnEntity(child);

            Random random = this.mother.getRNG();
            for (int i = 0; i < 7; ++i) {
                double d0 = random.nextGaussian() * 0.02D;
                double d1 = random.nextGaussian() * 0.02D;
                double d2 = random.nextGaussian() * 0.02D;
                this.world.spawnParticle(EnumParticleTypes.HEART, this.mother.posX + (double) (random.nextFloat() * this.mother.width * 2.0F) - (double) this.mother.width, this.mother.posY + 0.5D + (double) (random.nextFloat() * this.mother.height), this.mother.posZ + (double) (random.nextFloat() * this.mother.width * 2.0F) - (double) this.mother.width, d0, d1, d2);
            }

            if (this.world.getGameRules().getBoolean("doMobLoot")) {
                this.world.spawnEntity(new EntityXPOrb(this.world, this.mother.posX, this.mother.posY, this.mother.posZ, random.nextInt(2) + 1));
            }
        }
    }
}
