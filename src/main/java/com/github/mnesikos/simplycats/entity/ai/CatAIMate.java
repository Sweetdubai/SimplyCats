package com.github.mnesikos.simplycats.entity.ai;

import com.github.mnesikos.simplycats.configuration.SimplyCatsConfig;
import com.github.mnesikos.simplycats.entity.EntityCat;
import com.github.mnesikos.simplycats.entity.core.Genetics;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;

import java.util.List;

public class CatAIMate extends EntityAIBase {
    private final EntityCat CAT;
    private EntityCat TARGET;
    private final World WORLD;
    private final double MOVE_SPEED;
    private int MATE_DELAY;

    private List<EntityCat> LIST;
    private final double NEARBY_SIZE_CHECK = 16.0D;

    public CatAIMate(EntityCat entityCat, double speed) {
        this.CAT = entityCat;
        this.WORLD = entityCat.world;
        this.MOVE_SPEED = speed;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (this.CAT.getSex() == Genetics.Sex.FEMALE)
            return false;

        this.LIST = this.WORLD.getEntitiesWithinAABB(this.CAT.getClass(), this.CAT.getEntityBoundingBox().grow(NEARBY_SIZE_CHECK));
        if (this.LIST.size() >= SimplyCatsConfig.BREEDING_LIMIT)
            return false;

        else if ((this.TARGET != null && !this.TARGET.getBreedingStatus(EntityCat.BreedingStatus.IN_HEAT)) || (this.CAT.getMateTimer() > 0))
            return false;

        else {
            this.TARGET = this.getNearbyMate();
            return this.TARGET != null && this.CAT.getEntitySenses().canSee(this.TARGET);
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        boolean maleCooldownCheck = this.CAT.getSex() == Genetics.Sex.MALE && this.CAT.getMateTimer() == 0;
        boolean femaleHeatCheck = this.TARGET.getSex() == Genetics.Sex.FEMALE && this.TARGET.getBreedingStatus(EntityCat.BreedingStatus.IN_HEAT);
        this.LIST = this.WORLD.getEntitiesWithinAABB(this.CAT.getClass(), this.CAT.getEntityBoundingBox().grow(NEARBY_SIZE_CHECK));
        return maleCooldownCheck && this.TARGET.isEntityAlive() && femaleHeatCheck && this.MATE_DELAY < 60 && this.LIST.size() < SimplyCatsConfig.BREEDING_LIMIT && this.CAT.getEntitySenses().canSee(this.TARGET);
    }

    @Override
    public void resetTask() {
        this.TARGET = null;
        this.MATE_DELAY = 0;
        this.LIST.clear();
    }

    @Override
    public void updateTask() {
        this.CAT.getLookHelper().setLookPositionWithEntity(this.TARGET, 10.0F, (float) this.CAT.getVerticalFaceSpeed());
        this.TARGET.getLookHelper().setLookPositionWithEntity(this.CAT, 10.0F, (float) this.TARGET.getVerticalFaceSpeed());
        this.CAT.getNavigator().tryMoveToEntityLiving(this.TARGET, this.MOVE_SPEED);
        this.TARGET.getNavigator().tryMoveToEntityLiving(this.CAT, this.MOVE_SPEED);
        ++this.MATE_DELAY;

        if (this.MATE_DELAY >= 60 && this.CAT.getDistanceSq(this.TARGET) < 4.0D) {
            if (this.WORLD.rand.nextInt(4) <= 2) // 75% chance of success
                this.startPregnancy();
            this.CAT.setMateTimer(SimplyCatsConfig.MALE_COOLDOWN); // starts male cooldown
        }
    }

    private EntityCat getNearbyMate() {
        double d0 = Double.MAX_VALUE;
        EntityCat entityCat = null;

        if (this.CAT.getSex() == Genetics.Sex.MALE)
            for (EntityCat cat : this.WORLD.getEntitiesWithinAABB(this.CAT.getClass(), this.CAT.getEntityBoundingBox().grow(NEARBY_SIZE_CHECK))) {
                if (this.CAT.canMateWith(cat) && this.CAT.getDistanceSq(cat) < d0) {
                    entityCat = cat;
                    d0 = this.CAT.getDistanceSq(cat);
                }
            }

        return entityCat;
    }

    private void startPregnancy() {
        int litterSize;
        if (this.TARGET.getKittens() <= 0) {
            litterSize = this.WORLD.rand.nextInt(6) + 1; // at least 1 kitten, max of 6
        } else {
            litterSize = this.WORLD.rand.nextInt(6 - this.TARGET.getKittens()) + 1; // max of 6, minus already accrued kittens
        }
        this.TARGET.setBreedingStatus(EntityCat.BreedingStatus.PREGNANT, true);
        this.TARGET.setKittens(litterSize);
        this.TARGET.addFather(this.CAT, this.TARGET.getKittens()); // save father nbt data to mother cat for each kitten added to litterSize

        if (litterSize == 6 || this.TARGET.getKittens() == 6 || this.WORLD.rand.nextInt(4) == 0) { // full litter OR 25% chance ends heat
            this.TARGET.setBreedingStatus(EntityCat.BreedingStatus.IN_HEAT, false);
            this.TARGET.setTimeCycle(EntityCat.TimeCycle.PREGNANCY, SimplyCatsConfig.PREGNANCY_TIMER); // starts pregnancy timer
        }
    }
}
