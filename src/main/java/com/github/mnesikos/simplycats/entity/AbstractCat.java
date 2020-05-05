package com.github.mnesikos.simplycats.entity;

import com.github.mnesikos.simplycats.Ref;
import com.github.mnesikos.simplycats.entity.core.Genetics;
import com.github.mnesikos.simplycats.entity.core.Genetics.*;
import com.google.common.base.Optional;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractCat extends EntityTameable {
    private static final DataParameter<Byte> EYE_COLOR = EntityDataManager.createKey(AbstractCat.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> GENETICS = EntityDataManager.createKey(AbstractCat.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> WHITE_SPOTS = EntityDataManager.createKey(AbstractCat.class, DataSerializers.VARINT);
    private static final DataParameter<Optional<BlockPos>> HOME_POSITION  = EntityDataManager.createKey(AbstractCat.class, DataSerializers.OPTIONAL_BLOCK_POS);

    private String texturePrefix;
    private final String[] catTexturesArray = new String[12];

    private boolean purr;
    private int purrTimer;

    public AbstractCat(World world) {
        super(world);
        setPhenotype();
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(EYE_COLOR, (byte) 0);
        this.dataManager.register(GENETICS, 0);
        this.dataManager.register(WHITE_SPOTS, 0);
        this.dataManager.register(HOME_POSITION, Optional.absent());
    }

    private void setPhenotype() {
        //Bits of the genetics should look like this
        //1000 1000 10 1000 10 10 10 10 10 1000 1000 10
        this.dataManager.set(GENETICS,
                FurLength.init(rand).ordinal() | FurLength.init(rand).ordinal() << 1
                        | Eumelanin.init(rand).ordinal() << 2 | Eumelanin.init(rand).ordinal() << 4
                        | Phaeomelanin.init(rand) << 6
                        | Dilution.init(rand).ordinal() << 10 | Dilution.init(rand).ordinal() << 11
                        | DiluteMod.init(rand).ordinal() << 12 | DiluteMod.init(rand).ordinal() << 13
                        | Agouti.init(rand).ordinal() << 14 | Agouti.init(rand).ordinal() << 15
                        | Tabby.init(rand).ordinal() << 16 | Tabby.init(rand).ordinal() << 17
                        | Spotted.init(rand).ordinal() << 18 | Spotted.init(rand).ordinal() << 20
                        | Ticked.init(rand).ordinal() << 22 | Ticked.init(rand).ordinal() << 23
                        | Colorpoint.init(rand).ordinal() << 24 | Colorpoint.init(rand).ordinal() << 26
                        | White.init(rand).ordinal() << 28 | White.init(rand).ordinal() << 30);
        this.selectWhiteMarkings();
        this.dataManager.set(EYE_COLOR, (byte) selectEyeColor().ordinal());
    }

    private EyeColor selectEyeColor() {
        EyeColor color = EyeColor.values()[rand.nextInt(4)];
        // todo change whiteCheck == White.Spotting to a better check for proper white face check
        if (getWhite().contains(White.DOMINANT))
            color = EyeColor.values()[rand.nextInt(5)];
        if (getColorpoint().getPhenotype() == Colorpoint.COLORPOINT)
            color = EyeColor.BLUE;
        return color;
    }

    void selectWhiteMarkings() {
        int base;
        int[] values = new int[3];

        for (int j = 0; j <= 3; j++) {
            this.setWhitePaw(j, (byte) 0);
        }

        GeneticData<White> white = this.getWhite();
        if (white.contains(White.DOMINANT)) {
            base = 6;
            values[0] = 1;
        } else if (white.getFirst() == White.SPOTTING && white.getSecond() == White.SPOTTING) {
            base = rand.nextInt(2) + 4; //4-5
            if (base == 5) {
                values[0] = rand.nextInt(4) + 1;
                values[1] = rand.nextInt(6) + 1;
                if (values[0] > 1)
                    values[2] = rand.nextInt(3) + 1;
            }
            else if (base == 4) {
                values[0] = 1;
                values[1] = rand.nextInt(5) + 1;
            }
            if (rand.nextInt(10) == 0) { //10% chance for solid white
                base = 6;
                values[0] = 1;
            }
        } else if (white.contains(White.SPOTTING) && white.contains(White.NONE)) {
            base = rand.nextInt(3) + 1; //1-3
            values[0] = 1;
            if (base == 2 || base == 3)
                this.selectWhitePaws(base);
            if (base == 3)
                values[1] = rand.nextInt(5) + 1;
        } else throw new IllegalArgumentException("Error selecting white markings; " + this.getWhite());

        for (int i = 0; i < 3; i++) {
            int b = i == 1 && base == 3 || base == 4 ? 7 : base;
            this.setWhiteSpot(i, (byte)(b & (values[i] << 3)));
        }
    }

    private void selectWhitePaws(int base) {
        /*
         * boolean all is set so all 4 paws are white
         * making it more common than only random 1-4 white paws
         */
        boolean all = rand.nextInt(4) <= 2;
        for (int i = 0; i < 4; i++) {
            if (all || rand.nextInt(4) <= 2) {
                this.setWhitePaw(i, (byte) (base - 1));
            }
        }
    }

    public void setWhitePaw(int i, byte value) {
        setWhiteBitData(i, 2, 0x3, 0, 4, 0xFFF, value);
    }

    public byte getWhitePaw(int i) {
        return getWhiteBitData(i, 2, 0x3, 0);
    }

    public void setWhiteSpot(int i, byte value) {
        setWhiteBitData(i, 6, 0x3F, 8, 3, 0xFF, value);
    }

    public byte getWhiteSpot(int i) {
        return getWhiteBitData(i, 6, 0x3F, 8);
    }

    public Genetics.Sex getSex() {
        return getPhaeomelanin().contains(Phaeomelanin.MALE) ? Sex.MALE : Sex.FEMALE;
    }

    public Genetics.EyeColor getEyeColor() {
        return Genetics.EyeColor.values()[this.dataManager.get(EYE_COLOR)];
    }

    private void setWhiteBitData(int pos, int size, int prev, int offset, int length, int full, byte value) {
        int val = (value & prev) << ((pos * size) + offset);
        int k = 0;
        for (int j = 0; j < length; ++j) {
            if(j != pos) {
                k |= (getWhiteBitData(j, size, prev, offset) & prev) << ((j * size) + offset);
            }
        }
        this.dataManager.set(WHITE_SPOTS, (k | val | (((this.dataManager.get(WHITE_SPOTS) >> offset) & full) << offset)));
    }

    private byte getWhiteBitData(int pos, int size, int prev, int offset) {
        return (byte)((this.dataManager.get(WHITE_SPOTS) >> ((pos * size) + offset)) & prev);
    }

    private String getWhiteTextures(int i) {
        int data = this.getWhiteSpot(i);
        int base = data & 0x7;
        int value = data >> 3;

        if (base == 7) base = 34;

        return "white_" + base + "_body" + value;
    }

    private String getWhitePawTextures(int i) {
        return "white_" + (this.getWhitePaw(i) + 1) + "_paw" + (i + 1);
    }

    public boolean hasHomePos() {
        return this.dataManager.get(HOME_POSITION).isPresent();
    }

    public BlockPos getHomePos() {
        return this.dataManager.get(HOME_POSITION).or(this.getPosition());
    }

    public void setHomePos(BlockPos position) {
        this.dataManager.set(HOME_POSITION, Optional.of(position));
    }

    public void resetHomePos() {
        this.dataManager.set(HOME_POSITION, Optional.absent());
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("Genetics", this.dataManager.get(GENETICS));
        compound.setInteger("WhiteSpots", this.dataManager.get(WHITE_SPOTS));
        if (this.hasHomePos()) {
            compound.setInteger("HomePosX", this.getHomePos().getX());
            compound.setInteger("HomePosY", this.getHomePos().getY());
            compound.setInteger("HomePosZ", this.getHomePos().getZ());
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.dataManager.set(GENETICS, compound.getInteger("Genetics"));
        this.dataManager.set(WHITE_SPOTS, compound.getInteger("WhiteSpots"));
        if (compound.hasKey("HomePosX"))
            this.setHomePos(new BlockPos(compound.getInteger("HomePosX"), compound.getInteger("HomePosY"), compound.getInteger("HomePosZ")));
    }

    private int getGeneticData(int offset, int prev) {
        return (this.dataManager.get(GENETICS) >> offset) & prev;
    }

    private GeneticData<FurLength> getFurLength() {
        int length = getGeneticData(0, 0x3);
        return new GeneticData<>(FurLength.values()[length & 0x1], FurLength.values()[length >> 1], FurLength::getPhenotype, FurLength[]::new);
    }

    private GeneticData<Eumelanin> getEumelanin() {
        int length = getGeneticData(2, 0xF);
        return new GeneticData<>(Eumelanin.values()[length & 0x3], Eumelanin.values()[length >> 2], Eumelanin::getPhenotype, Eumelanin[]::new);
    }

    private GeneticData<Phaeomelanin> getPhaeomelanin() {
        int length = getGeneticData(6, 0xF);
        return new GeneticData<>(Phaeomelanin.values()[length & 0x3], Phaeomelanin.values()[length >> 2], Phaeomelanin::getPhenotype, Phaeomelanin[]::new);
    }

    private GeneticData<Dilution> getDilution() {
        int length = getGeneticData(10, 0x3);
        return new GeneticData<>(Dilution.values()[length & 0x1], Dilution.values()[length >> 1], Dilution::getPhenotype, Dilution[]::new);
    }

    private GeneticData<DiluteMod> getDiluteMod() {
        int length = getGeneticData(12, 0x3);
        return new GeneticData<>(DiluteMod.values()[length & 0x1], DiluteMod.values()[length >> 1], DiluteMod::getPhenotype, DiluteMod[]::new);
    }

    private GeneticData<Agouti> getAgouti() {
        int length = getGeneticData(14, 0x3);
        return new GeneticData<>(Agouti.values()[length & 0x1], Agouti.values()[length >> 1], Agouti::getPhenotype, Agouti[]::new);
    }

    private GeneticData<Tabby> getTabby() {
        int length = getGeneticData(16, 0x3);
        return new GeneticData<>(Tabby.values()[length & 0x1], Tabby.values()[length >> 1], Tabby::getPhenotype, Tabby[]::new);
    }

    private GeneticData<Spotted> getSpotted() {
        int length = getGeneticData(18, 0xF);
        return new GeneticData<>(Spotted.values()[length & 0x3], Spotted.values()[length >> 2], Spotted::getPhenotype, Spotted[]::new);
    }

    private GeneticData<Ticked> getTicked() {
        int length = getGeneticData(22, 0x3);
        return new GeneticData<>(Ticked.values()[length & 0x1], Ticked.values()[length >> 1], Ticked::getPhenotype, Ticked[]::new);
    }

    GeneticData<Colorpoint> getColorpoint() {
        int length = getGeneticData(24, 0xF);
        return new GeneticData<>(Colorpoint.values()[length & 0x3], Colorpoint.values()[length >> 2], Colorpoint::getPhenotype, Colorpoint[]::new);
    }

    GeneticData<White> getWhite() {
        int length = getGeneticData(28, 0xF);
        return new GeneticData<>(White.values()[length & 0x3], White.values()[length >> 2], White::getPhenotype, White[]::new);
    }

    /*public boolean canWander() {
        if (this.hasHomePos())
            return this.getDistanceSq(this.getHomePos()) < SimplyCatsConfig.WANDER_AREA_LIMIT;
        else
            return true;
    }*/

    private void resetTexturePrefix() {
        this.texturePrefix = null;
    }

    @SideOnly(Side.CLIENT)
    private void setCatTexturePaths() {
        String solid = getEumelanin().getPhenotype().getName();
        GeneticData<Phaeomelanin> phaeomelanin = getPhaeomelanin();
        if (phaeomelanin.getPhenotype() == Phaeomelanin.RED)
            solid = "red";
        if (this.getDilution().getPhenotype() == Dilution.DILUTE) {
            solid = solid + "_dilute";
            /*if (this.getPhenotype(DILUTE_MOD).equalsIgnoreCase(DiluteMod.CARAMELIZED.toString().toLowerCase()))
                solid = solid + "_" + this.getPhenotype(DILUTE_MOD);*/
        }

        String tabby = getTabby().getPhenotype().getName() + "_" + solid;
        GeneticData<Spotted> spotted = this.getSpotted();
        if (spotted.contains(Spotted.SPOTTED))
            tabby = spotted.getPhenotype().getName() + "_" + tabby;
        if (this.getTicked().getPhenotype() == Ticked.TICKED)
            tabby = "ticked_" + solid;

        String tortie = "";
        if (phaeomelanin.getPhenotype() == Phaeomelanin.TORTOISESHELL) {
            tortie = "tortoiseshell_" + (tabby.replace(("_" + solid), ""));
            if (getDilution().getPhenotype() == Dilution.DILUTE) {
                tortie = tortie + "_dilute";
                /*if (this.getPhenotype(DILUTE_MOD).equalsIgnoreCase(DiluteMod.CARAMELIZED.toString().toLowerCase()))
                    tortie = tortie + "_" + this.getPhenotype(DILUTE_MOD);*/
            }
        }

        if (phaeomelanin.getPhenotype() != Phaeomelanin.RED && getAgouti().getPhenotype() == Agouti.SOLID)
            tabby = "";

        String colorpoint = "";
        GeneticData<Colorpoint> colorpointData = getColorpoint();
        if (colorpointData.getPhenotype() != Colorpoint.NOT_POINTED) {
            colorpoint = colorpointData.getPhenotype().getName();
            if (!tabby.equals("") && phaeomelanin.getPhenotype() != Phaeomelanin.RED)
                colorpoint = colorpointData + "_tabby";
            else if (solid.equalsIgnoreCase(Eumelanin.BLACK.toString()))
                colorpoint = colorpointData + "_" + solid;
            else if (phaeomelanin.getPhenotype() == Phaeomelanin.RED)
                colorpoint = colorpointData + "_red";
            if (!tortie.equals(""))
                tortie = tortie + "_point";
        }

        this.catTexturesArray[0] = Ref.MODID + ":textures/entity/cat/solid/" + solid + ".png";
        this.catTexturesArray[1] = tabby.isEmpty() ? null : (Ref.MODID + ":textures/entity/cat/tabby/" + tabby + ".png");
        this.catTexturesArray[2] = tortie.isEmpty() ? null : (Ref.MODID + ":textures/entity/cat/tortie/" + tortie + ".png");
        this.catTexturesArray[3] = colorpoint.isEmpty() ? null : (Ref.MODID + ":textures/entity/cat/colorpoint/" + colorpoint + ".png");

        for (int i = 0; i < 3; i++) {
            String texture = this.getWhiteTextures(i);
            this.catTexturesArray[i + 4] = texture.isEmpty() ? null : (Ref.MODID + ":textures/entity/cat/white/new/" + texture + ".png");
        }

        for (int i = 0; i < 4; i++) {
            String texture = this.getWhitePawTextures(i);
            this.catTexturesArray[i + 7] = texture.isEmpty() ? null : (Ref.MODID + ":textures/entity/cat/white/new/" + texture + ".png");
        }

        this.catTexturesArray[11] = Ref.MODID + ":textures/entity/cat/eyes/" + getEyeColor().getName() + ".png";
        this.texturePrefix = "cat/" + solid + tabby + tortie + colorpointData +
                this.getWhiteTextures(0) + this.getWhiteTextures(1) + this.getWhiteTextures(2) +
                this.getWhitePawTextures(0) + this.getWhitePawTextures(1) +
                this.getWhitePawTextures(2) + this.getWhitePawTextures(3) +
                getEyeColor().getName();
    }

    @SideOnly(Side.CLIENT)
    public String getCatTexture() {
        if (this.texturePrefix == null)
            this.setCatTexturePaths();

        return this.texturePrefix;
    }

    @SideOnly(Side.CLIENT)
    public String[] getTexturePaths() {
        if (this.texturePrefix == null)
            this.setCatTexturePaths();

        return this.catTexturesArray;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.purr) {
            if (purrTimer == 0) {
                this.purr = false;
                this.purrTimer = 0;
            }
        }

        if (this.getAttackTarget() == null) {
            List<EntityZombie> zombies = this.world.getEntitiesWithinAABB(EntityZombie.class, this.getEntityBoundingBox().grow(4.0D, 3.0D, 4.0D));
            List<AbstractSkeleton> skeletons = this.world.getEntitiesWithinAABB(AbstractSkeleton.class, this.getEntityBoundingBox().grow(4.0D, 3.0D, 4.0D));
            if ((!zombies.isEmpty() || !skeletons.isEmpty()) && this.rand.nextInt(400) == 0) {
                this.playSound(SoundEvents.ENTITY_CAT_HISS, this.getSoundVolume() / 2.0F, this.getSoundPitch());
            }
        }

        if (this.world.isRemote && this.dataManager.isDirty()) {
            this.dataManager.setClean();
            this.resetTexturePrefix();
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (this.purr && purrTimer > 0) {
            --purrTimer;
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty()) {
            if (stack.getItem() == Items.STRING && player.isSneaking()) {
                if (this.world.isRemote) {
                    GeneticData[] arr = new GeneticData[] {getFurLength(), getEumelanin(), getPhaeomelanin(), getDilution(), getDiluteMod(), getAgouti(), getTabby(), getSpotted(), getTicked(), getColorpoint(), getWhite()};
                    player.sendMessage(new TextComponentString(getEyeColor().getName()));
                    for (GeneticData data : arr) {
                        player.sendMessage(new TextComponentString(data.toString() + ": " + data.getPhenotype().getName()));
                    }
                    player.sendMessage(new TextComponentString(this.getWhiteTextures(0) + ", " + this.getWhiteTextures(1)
                            + ", " + this.getWhiteTextures(2)));
                    player.sendMessage(new TextComponentString(this.getWhitePawTextures(0) + ", " + this.getWhitePawTextures(1)
                            + ", " + this.getWhitePawTextures(2) + ", " + this.getWhitePawTextures(3)));
                }
                return true;
            }
        }

        if (!this.purr && this.rand.nextInt(10) == 0) { // 1/10th chance an interaction will result in purrs
            this.purr = true;
            this.purrTimer = (this.rand.nextInt(61) + 30) * 20; // random range of 600 to 1800 ticks (0.5 to 1.5 IRL minutes)
        }

        return false;
    }

    @Nullable
    @Override
    public EntityAgeable createChild(EntityAgeable parFather) {
        AbstractCat father = (AbstractCat) parFather;
        EntityCat child = new EntityCat(this.world);

        FurLength[] matFur = this.getFurLength().getValues();
        FurLength[] patFur = father.getFurLength().getValues();

        Eumelanin[] matEum = this.getEumelanin().getValues();
        Eumelanin[] patEum = father.getEumelanin().getValues();

        Phaeomelanin[] matPhae = this.getPhaeomelanin().getValues();
        Phaeomelanin[] patPhae = father.getPhaeomelanin().getValues();

        Dilution[] matDil = this.getDilution().getValues();
        Dilution[] patDil = father.getDilution().getValues();

        DiluteMod[] matDilm = this.getDiluteMod().getValues();
        DiluteMod[] patDilm = father.getDiluteMod().getValues();

        Agouti[] matAgo = this.getAgouti().getValues();
        Agouti[] patAgo = father.getAgouti().getValues();

        Tabby[] matTab = this.getTabby().getValues();
        Tabby[] patTab = father.getTabby().getValues();

        Spotted[] matSpot = this.getSpotted().getValues();
        Spotted[] patSpot = father.getSpotted().getValues();

        Ticked[] matTick = this.getTicked().getValues();
        Ticked[] patTick = father.getTicked().getValues();

        Colorpoint[] matPoint = this.getColorpoint().getValues();
        Colorpoint[] patPoint = father.getColorpoint().getValues();

        White[] matWhite = this.getWhite().getValues();
        White[] patWhite = father.getWhite().getValues();

        child.dataManager.set(GENETICS,
                matFur[rand.nextInt(2)].ordinal() | patFur[rand.nextInt(2)].ordinal() << 1
                        | matEum[rand.nextInt(2)].ordinal() << 2 | patEum[rand.nextInt(2)].ordinal() << 4
                        | matPhae[rand.nextInt(2)].ordinal() << 6 |  patPhae[rand.nextInt(2)].ordinal() << 8
                        | matDil[rand.nextInt(2)].ordinal() << 10 | patDil[rand.nextInt(2)].ordinal() << 11
                        | matDilm[rand.nextInt(2)].ordinal() << 12 | patDilm[rand.nextInt(2)].ordinal() << 13
                        | matAgo[rand.nextInt(2)].ordinal() << 14 | patAgo[rand.nextInt(2)].ordinal() << 15
                        | matTab[rand.nextInt(2)].ordinal() << 16 | patTab[rand.nextInt(2)].ordinal() << 17
                        | matSpot[rand.nextInt(2)].ordinal() << 18 | patSpot[rand.nextInt(2)].ordinal() << 20
                        | matTick[rand.nextInt(2)].ordinal() << 22 | patTick[rand.nextInt(2)].ordinal() << 23
                        | matPoint[rand.nextInt(2)].ordinal() << 24 | patPoint[rand.nextInt(2)].ordinal() << 26
                        | matWhite[rand.nextInt(2)].ordinal() << 28 | patWhite[rand.nextInt(2)].ordinal() << 30);

        child.selectWhiteMarkings();

        int eyesMin;
        int eyesMax;
        int matEye = this.getEyeColor().ordinal();
        int patEye = father.getEyeColor().ordinal();
        if (matEye > patEye) {
            eyesMin = patEye - 1;
            eyesMax = matEye;
        } else {
            eyesMin = matEye - 1;
            eyesMax = patEye;
        }
        eyesMin = Math.max(eyesMin, 0);
        if (child.getWhite().contains(White.DOMINANT))
            eyesMax = 4;
        else
            eyesMax = eyesMax >= 4 ? (eyesMin < 3 ? eyesMin + 1 : 3) : eyesMax;
        int eyes = rand.nextInt((eyesMax - eyesMin) + 1) + eyesMin;
        GeneticData<Colorpoint> point = child.getColorpoint();
        EyeColor eye = point.getFirst() == Colorpoint.COLORPOINT && point.getSecond() == Colorpoint.COLORPOINT ? EyeColor.BLUE : EyeColor.values()[matEye == 4 && patEye == 4 ? (eyesMax == 4 ? 4 : rand.nextInt(4)) : eyes];
        child.dataManager.set(EYE_COLOR, (byte) eye.ordinal());
        child.setTamed(this.isTamed());
        if (this.isTamed()) {
            child.setOwnerId(this.getOwnerId());
            if (this.hasHomePos())
                child.setHomePos(this.getHomePos());
        }

        return child;
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isInLove() || this.purr) {
            return SoundEvents.ENTITY_CAT_PURR;
        } else {
            if (this.rand.nextInt(10) == 0) {
                if (this.rand.nextInt(10) == 0)
                    return SoundEvents.ENTITY_CAT_PURREOW;
                else
                    return SoundEvents.ENTITY_CAT_AMBIENT;
            }
        }
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_CAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_CAT_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public String getName() {
        if (this.hasCustomName())
            return this.getCustomNameTag();
        else
            return this.isTamed() ? I18n.format("entity.Cat.name") : super.getName();
    }

    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return null;
    }
}
