package com.github.mnesikos.simplycats.entity;

import com.github.mnesikos.simplycats.Ref;
import com.github.mnesikos.simplycats.configuration.SimplyCatsConfig;
import com.github.mnesikos.simplycats.entity.ai.CatAIBirth;
import com.github.mnesikos.simplycats.entity.ai.CatAIMate;
import com.github.mnesikos.simplycats.entity.ai.CatAIWander;
import com.github.mnesikos.simplycats.entity.core.Genetics;
import com.github.mnesikos.simplycats.init.ModItems;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EntityCat extends AbstractCat {
    public static final DataParameter<String> MOTHER = EntityDataManager.createKey(EntityCat.class, DataSerializers.STRING);
    public static final DataParameter<String> FATHER = EntityDataManager.createKey(EntityCat.class, DataSerializers.STRING);

    private static final DataParameter<Byte> DATA = EntityDataManager.createKey(EntityCat.class, DataSerializers.BYTE);
    private static final DataParameter<Byte> KITTENS = EntityDataManager.createKey(EntityCat.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> MATE_TIMER = EntityDataManager.createKey(EntityCat.class, DataSerializers.VARINT);

    private EntityAITempt aiTempt;

    public EntityCat(World world) {
        super(world);
        this.setSize(0.6F, 0.8F);
    }

    @Override
    protected void initEntityAI() {
        this.aiSit = new EntityAISit(this);
        this.aiTempt = new EntityAITempt(this, 1.2D, ModItems.TREAT_BAG, false);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiSit);
        this.tasks.addTask(3, this.aiTempt);
        if (!this.isSitting())
            this.tasks.addTask(3, new EntityAIFollowParent(this, 1.0D));
        if (!this.isFixed())
            this.tasks.addTask(3, new CatAIMate(this, 1.2D));
        this.tasks.addTask(4, new CatAIBirth(this));
        this.tasks.addTask(5, new EntityAILeapAtTarget(this, 0.4F));
        this.tasks.addTask(6, new EntityAIOcelotAttack(this));
        this.tasks.addTask(7, new CatAIWander(this, 1.0D));
        //this.tasks.addTask(8, new CatAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityLiving.class, 7.0F));
        this.tasks.addTask(10, new EntityAILookIdle(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.7D);
        getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0.5D);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(MOTHER, "");
        this.dataManager.register(FATHER, "");

        this.dataManager.register(DATA, (byte)0);
        this.dataManager.register(KITTENS, (byte) 0);
        this.dataManager.register(MATE_TIMER, 0);
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        if (!this.world.isRemote)
            if (this.isTamed())
                this.aiSit.setSitting(!this.isSitting());
        if (this.getSex() == Genetics.Sex.FEMALE && !this.isFixed())
            this.setTimeCycle(TimeCycle.END, this.world.rand.nextInt(SimplyCatsConfig.HEAT_COOLDOWN));
        return super.onInitialSpawn(difficulty, livingdata);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!this.world.isRemote && !this.isChild() && !this.isFixed() && this.getSex() == Genetics.Sex.FEMALE) { //if female & adult & not fixed
            if (this.getBreedingStatus(BreedingStatus.IN_HEAT)) //if in heat
                if (this.getMateTimer() <= 0) { //and timer is finished (reaching 0 after being in positives)
                    if (!this.getBreedingStatus(BreedingStatus.PREGNANT)) //and not pregnant
                        setTimeCycle(TimeCycle.END, SimplyCatsConfig.HEAT_COOLDOWN); //sets out of heat for 16 (default) minecraft days
                    else { //or if IS pregnant
                        setTimeCycle(TimeCycle.PREGNANCY, SimplyCatsConfig.PREGNANCY_TIMER); //and heat time runs out, starts pregnancy timer for birth
                        this.setBreedingStatus(BreedingStatus.IN_HEAT, false); //sets out of heat
                    }
                }
            if (!this.getBreedingStatus(BreedingStatus.IN_HEAT)) { //if not in heat
                if (this.getMateTimer() >= 0) { //and timer is finished (reaching 0 after being in negatives)
                    if (!this.getBreedingStatus(BreedingStatus.PREGNANT)) //and not pregnant
                        setTimeCycle(TimeCycle.START, SimplyCatsConfig.HEAT_TIMER); //sets in heat for 2 minecraft days
                }
            }
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.isChild() && !this.isFixed()) { //if not a child & not fixed
            int mateTimer = this.getMateTimer();
            if (this.getSex() == Genetics.Sex.FEMALE) {
                if (this.getBreedingStatus(BreedingStatus.IN_HEAT) || this.getBreedingStatus(BreedingStatus.PREGNANT)) {
                    --mateTimer;
                    if (this.getBreedingStatus(BreedingStatus.IN_HEAT)) {
                        if (mateTimer % 10 == 0) {

                            double d0 = this.rand.nextGaussian() * 0.02D;
                            double d1 = this.rand.nextGaussian() * 0.02D;
                            double d2 = this.rand.nextGaussian() * 0.02D;
                            this.world.spawnParticle(EnumParticleTypes.HEART, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
                        }
                    }
                }
                else if (!this.getBreedingStatus(BreedingStatus.IN_HEAT) && !this.getBreedingStatus(BreedingStatus.PREGNANT))
                    ++mateTimer;
            } else if (this.getSex() == Genetics.Sex.MALE) {
                if (mateTimer > 0)
                    --mateTimer;
                else mateTimer = 0;
            }
            this.setMateTimer(mateTimer);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable(source)) {
            return false;
        } else {
            if (this.aiSit != null) {
                this.aiSit.setSitting(false);
            }

            return super.attackEntityFrom(source, amount);
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        return entity.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
    }

    public void setParent(DataParameter<String> parent, String name) {
        this.dataManager.set(parent, name);
    }

    public String getParent(DataParameter<String> parent) {
        return this.dataManager.get(parent);
    }

    public void setFixed(boolean fixed) {
        this.dataManager.set(DATA, (byte) (this.dataManager.get(DATA) & (fixed ? 0x1 : -0x2)));
    }

    public boolean isFixed() {
        return (this.dataManager.get(DATA) & 0x1) > 0;
    }

    public void setTimeCycle(TimeCycle cycle, int time) {
        if (cycle == TimeCycle.START) {
            this.setBreedingStatus(BreedingStatus.IN_HEAT, true);
            this.setMateTimer(time);
        }
        if (cycle == TimeCycle.END) {
            this.setBreedingStatus(BreedingStatus.IN_HEAT, false);
            this.setMateTimer(-time);
        }
        if (cycle == TimeCycle.PREGNANCY) {
            this.setMateTimer(time);
        }
    }

    public void setBreedingStatus(BreedingStatus status, boolean value) {
        if (status == BreedingStatus.IN_HEAT) {
            this.dataManager.set(DATA, (byte) (this.dataManager.get(DATA) & (value ? 0x2 : -0x3)));
        } else {
            this.dataManager.set(DATA, (byte) (this.dataManager.get(DATA) & (value ? 0x4 : -0x5)));
        }
    }

    public boolean getBreedingStatus(BreedingStatus status) {
        return (this.dataManager.get(DATA) & (status == BreedingStatus.IN_HEAT ? 0x2 : 0x4)) > 0;
    }

    public void setMateTimer(int time) {
        this.dataManager.set(MATE_TIMER, time);
    }

    public int getMateTimer() {
        return this.dataManager.get(MATE_TIMER);
    }

    public void setKittens(int kittens) {
        if (getKittens() <= 0 || kittens == 0)
            this.dataManager.set(KITTENS, (byte) kittens);
        else if (getKittens() > 0)
            this.dataManager.set(KITTENS, (byte) (this.getKittens() + kittens));
    }

    public int getKittens() {
        return this.dataManager.get(KITTENS);
    }

    public void addFather(EntityCat father, int size) {
        for (int i = 0; i < size; i++) {
            //Removed this.getEntityData().getCompoundTag("Father" + i) == null as I'm pretty sure it's an impossible condition
            if(!this.getEntityData().hasKey("Father" + i)) {
                this.getEntityData().setTag("Father" + i, father.writeToNBT(new NBTTagCompound()));
            }
        }
    }

    private void setFather(int i, NBTBase father) {
        if (this.getEntityData().hasKey("Father" + i))
            this.getEntityData().setTag("Father" + i, father);
    }

    public NBTTagCompound getFather(int i) {
        return this.getEntityData().getCompoundTag("Father" + i);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setByte("Data", this.dataManager.get(DATA));
        if (this.getSex() == Genetics.Sex.FEMALE) {
            nbt.setInteger("Kittens", this.getKittens());
            for (int i = 0; i < 5; i++)
                nbt.setTag("Father" + i, this.getFather(i));
        }
        nbt.setInteger("Timer", this.getMateTimer());
        nbt.setString("Mother", this.getParent(MOTHER));
        nbt.setString("Father", this.getParent(FATHER));
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.dataManager.set(DATA, nbt.getByte("Data"));
        if (this.getSex() == Genetics.Sex.FEMALE && !this.isFixed()) {
            this.setBreedingStatus(BreedingStatus.IN_HEAT, nbt.getBoolean("InHeat"));
            this.setBreedingStatus(BreedingStatus.PREGNANT, nbt.getBoolean("IsPregnant"));
            this.setKittens(nbt.getInteger("Kittens"));
            for (int i = 0; i < 5; i++) {
                this.setFather(i, nbt.getTag("Father" + i));
            }
        }
        if (!this.isFixed())
            this.setMateTimer(nbt.getInteger("Timer"));
        this.setParent(MOTHER, nbt.getString("Mother"));
        this.setParent(FATHER, nbt.getString("Father"));
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public boolean canMateWith(EntityAnimal target) {
        if (target == this)
            return false;
        if (!(target instanceof EntityCat))
            return false;
        if (target.isChild() || this.isChild())
            return false;
        if (this.isSitting() || ((EntityCat) target).isSitting())
            return false;

        EntityCat mate = (EntityCat) target;
        if (mate.isFixed() || this.isFixed())
            return false;

        if ((this.getSex() == Genetics.Sex.MALE && this.getMateTimer() == 0)) // if (this) is male & not on a cooldown
            return (mate.getSex() == Genetics.Sex.FEMALE && mate.getBreedingStatus(BreedingStatus.IN_HEAT)); // returns true if (mate) is female & in heat
        else
            return false;
    }

    @Override
    public boolean isBreedingItem(ItemStack item) {
        return false;
    }

    @Override
    public EntityCat createChild(EntityAgeable parFather) {
        return (EntityCat) super.createChild(parFather);
    }

    private boolean isFoodItem(ItemStack item) {
        return Ref.catFoodItems(item);
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!stack.isEmpty()) {
            if (this.isTamed() && this.isOwner(player)) {
                if (stack.getItem() == Items.BLAZE_POWDER && player.isSneaking()) {
                    if (!this.isFixed() && this.getMateTimer() != 0) {
                        this.setMateTimer(this.getMateTimer() / 2); // heat inducer, used on females not in heat to quicken the process
                        if (!player.capabilities.isCreativeMode)
                            stack.shrink(1);
                        if (stack.getCount() <= 0)
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
                    }
                    return true;

                }
            }

            if (!this.isTamed() || (this.isTamed() && this.isOwner(player))) {
                if (stack.getItem() == Items.SHEARS && player.isSneaking()) {
                    if (!this.isFixed()) {
                        this.setFixed(true);
                        if (this.world.isRemote) {
                            player.sendMessage(new TextComponentString(this.getName() + " " + I18n.format("chat.info.success_fixed_" + this.getSex().getName())));
                        }
                    }
                    return true;
                }
            }

            if (isFoodItem(stack)) {
                ItemFood food = (ItemFood) stack.getItem();
                if (this.getHealth() < this.getMaxHealth())
                    this.heal((float) food.getHealAmount(stack));
                if (!player.capabilities.isCreativeMode)
                    stack.shrink(1);
                if (stack.getCount() <= 0)
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
                return true;
            }

            if (stack.getItem() == Items.STICK && player.isSneaking()) {
                if (this.world.isRemote) {
                    if (this.isFixed()) {
                        if (this.getSex() == Genetics.Sex.FEMALE)
                            player.sendMessage(new TextComponentTranslation("chat.info.fixed_female"));
                        else
                            player.sendMessage(new TextComponentTranslation("chat.info.fixed_male"));
                    } else if (this.getSex() == Genetics.Sex.FEMALE && this.getBreedingStatus(BreedingStatus.PREGNANT)) {
                        if (!this.getBreedingStatus(BreedingStatus.IN_HEAT))
                            player.sendMessage(new TextComponentString(I18n.format("chat.info.pregnant") + " " + this.getMateTimer()/* + parents + this.getParent("mother") + "/" + this.getParent("father")*/));
                        else
                            player.sendMessage(new TextComponentString(I18n.format("chat.info.pregnant_heat") + " " + this.getMateTimer()));
                    } else if (this.getSex() == Genetics.Sex.FEMALE && this.getBreedingStatus(BreedingStatus.IN_HEAT))
                        player.sendMessage(new TextComponentString(I18n.format("chat.info.in_heat") + " " + this.getMateTimer()/* + parents + this.getParent("mother") + "/" + this.getParent("father")*/));
                    else if (this.getSex() == Genetics.Sex.FEMALE && !this.getBreedingStatus(BreedingStatus.IN_HEAT))
                        player.sendMessage(new TextComponentString(I18n.format("chat.info.not_in_heat") + " " + this.getMateTimer()/* + parents + this.getParent("mother") + "/" + this.getParent("father")*/));
                    else if (this.getSex() == Genetics.Sex.MALE)
                        player.sendMessage(new TextComponentString(I18n.format("chat.info.male") + " " + this.getMateTimer()/* + parents + this.getParent("mother") + "/" + this.getParent("father")*/));
                }
                return true;

            } else if (stack.getItem() == Items.BONE && player.isSneaking()) {
                if (this.world.isRemote) {
                    if (this.getSex() == Genetics.Sex.FEMALE && this.getBreedingStatus(BreedingStatus.PREGNANT))
                        player.sendMessage(new TextComponentString(new TextComponentTranslation("chat.info.kitten_count").getFormattedText() + " " + this.getKittens()));
                }
                return true;

            } else if ((this.aiTempt == null || this.aiTempt.isRunning()) && stack.getItem() == ModItems.TREAT_BAG && player.getDistanceSq(this) < 9.0D) {
                if (player.isSneaking()) {
                    if (this.hasHomePos()) {
                        this.resetHomePos();
                        if (this.world.isRemote)
                            player.sendMessage(new TextComponentString(new TextComponentTranslation("chat.info.remove_home").getFormattedText() + " " + this.getName()));
                    } else {
                        this.setHomePos(new BlockPos(this));
                        if (this.world.isRemote)
                            player.sendMessage(new TextComponentString(this.getName() +
                                    new TextComponentTranslation("chat.info.set_home").getFormattedText() +
                                    " " + getHomePos().getX() + ", " + getHomePos().getY() + ", " + getHomePos().getZ()));
                    }
                    return true;
                } else {
                    if (this.hasHomePos())
                        if (this.world.isRemote)
                            player.sendMessage(new TextComponentString(getHomePos().getX() + ", " + getHomePos().getY() + ", " + getHomePos().getZ()));
                }

            }
        }

        if (!this.world.isRemote && this.isOwner(player) && !player.isSneaking()) {
            if (stack.isEmpty() || (!this.isBreedingItem(stack) && !this.isFoodItem(stack))) {
                this.aiSit.setSitting(!this.isSitting());
                this.navigator.clearPath();
                this.setAttackTarget(null);
            }
        }

        return super.processInteract(player, hand);
    }

    public enum TimeCycle {
        START,
        END,
        PREGNANCY
    }

    public enum BreedingStatus {
        IN_HEAT,
        PREGNANT
    }
}
