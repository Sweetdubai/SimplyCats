package com.github.mnesikos.simplycats.client.render.entity;

import com.github.mnesikos.simplycats.Ref;
import com.github.mnesikos.simplycats.client.model.entity.CatSCModel;
import com.github.mnesikos.simplycats.client.render.entity.feature.CatMarkingFeatureRenderer;
import com.github.mnesikos.simplycats.entity.CatSCEntity;
import com.github.mnesikos.simplycats.entity.core.Genetics;
import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class CatSCEntityRenderer extends MobEntityRenderer<CatSCEntity, CatSCModel<CatSCEntity>> {
    private static final Map<Genetics.Eumelanin, Identifier> EUMELANIN = Util.make(Maps.newEnumMap(Genetics.Eumelanin.class), (enumMap) -> {
        enumMap.put(Genetics.Eumelanin.BLACK, new Identifier(Ref.MOD_ID, "textures/entity/cat/solid/black.png"));
        enumMap.put(Genetics.Eumelanin.CHOCOLATE, new Identifier(Ref.MOD_ID, "textures/entity/cat/solid/chocolate.png"));
        enumMap.put(Genetics.Eumelanin.CINNAMON, new Identifier(Ref.MOD_ID, "textures/entity/cat/solid/cinnamon.png"));
    });
    private static final Identifier PHAEOMELANIN = new Identifier(Ref.MOD_ID, "textures/entity/cat/solid/red.png");
    private static final Identifier DILUTION_RED = new Identifier(Ref.MOD_ID, "textures/entity/cat/solid/red_dilute.png");
    //    private static final Identifier DILUTION_MOD_RED = new Identifier(Ref.MOD_ID, "textures/entity/cat/solid/red_dilute_caramelized.png");
    private static final Map<Genetics.Eumelanin, Identifier> DILUTION = Util.make(Maps.newEnumMap(Genetics.Eumelanin.class), (enumMap) -> {
        enumMap.put(Genetics.Eumelanin.BLACK, new Identifier(Ref.MOD_ID, "textures/entity/cat/solid/black_dilute.png"));
        enumMap.put(Genetics.Eumelanin.CHOCOLATE, new Identifier(Ref.MOD_ID, "textures/entity/cat/solid/chocolate_dilute.png"));
        enumMap.put(Genetics.Eumelanin.CINNAMON, new Identifier(Ref.MOD_ID, "textures/entity/cat/solid/cinnamon_dilute.png"));
    });
    /*private static final Map<Genetics.Eumelanin, Identifier> DILUTION_MOD = Util.make(Maps.newEnumMap(Genetics.Eumelanin.class), (enumMap) -> {
        enumMap.put(Genetics.Eumelanin.BLACK, new Identifier(Ref.MOD_ID, "textures/entity/cat/solid/black_dilute_caramelized.png"));
        enumMap.put(Genetics.Eumelanin.CHOCOLATE, new Identifier(Ref.MOD_ID, "textures/entity/cat/solid/chocolate_dilute_caramelized.png"));
        enumMap.put(Genetics.Eumelanin.CINNAMON, new Identifier(Ref.MOD_ID, "textures/entity/cat/solid/cinnamon_dilute_caramelized.png"));
    });*/

    public CatSCEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new CatSCModel<>(), 0.2f);
        this.addFeature(new CatMarkingFeatureRenderer(this));
    }

    @Override
    protected void scale(CatSCEntity entity, MatrixStack matrices, float amount) {
        super.scale(entity, matrices, amount);
        matrices.scale(0.8F, 0.9F, 0.8F);
    }

    @Override
    public Identifier getTexture(CatSCEntity entity) {
        if (Genetics.Phaeomelanin.getPhenotype(entity.getPhaeomelanin()).equalsIgnoreCase(Genetics.Phaeomelanin.RED.toString())) {
            if (Genetics.Dilution.getPhenotype(entity.getDilution()).equalsIgnoreCase(Genetics.Dilution.DILUTE.toString()))
                return DILUTION_RED;
            else
                return PHAEOMELANIN;

        } else if (Genetics.Dilution.getPhenotype(entity.getDilution()).equalsIgnoreCase(Genetics.Dilution.DILUTE.toString()))
            return DILUTION.get(getEumelanin(entity));

        return EUMELANIN.get(getEumelanin(entity));
    }

    private Genetics.Eumelanin getEumelanin(CatSCEntity entity) {
        String[] value = entity.getEumelanin().split("-");
        if (value[0].equals("B") || value[1].equals("B"))
            return (Genetics.Eumelanin.BLACK);
        else if (value[0].equals("b") || value[1].equals("b"))
            return (Genetics.Eumelanin.CHOCOLATE);
        else
            return (Genetics.Eumelanin.CINNAMON);
    }
}
