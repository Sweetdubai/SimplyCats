package com.github.mnesikos.simplycats.client.render.entity.feature;

import com.github.mnesikos.simplycats.Ref;
import com.github.mnesikos.simplycats.client.model.entity.CatSCModel;
import com.github.mnesikos.simplycats.entity.CatSCEntity;
import com.github.mnesikos.simplycats.entity.core.Genetics;
import com.google.common.collect.Maps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class CatEyesFeatureRenderer extends FeatureRenderer<CatSCEntity, CatSCModel<CatSCEntity>> {
    private static final Map<Genetics.EyeColor, Identifier> EYE_COLOR = Util.make(Maps.newEnumMap(Genetics.EyeColor.class), (enumMap) -> {
        enumMap.put(Genetics.EyeColor.COPPER, new Identifier(Ref.MOD_ID, "textures/entity/cat/eyes/copper.png"));
        enumMap.put(Genetics.EyeColor.GOLD, new Identifier(Ref.MOD_ID, "textures/entity/cat/eyes/gold.png"));
        enumMap.put(Genetics.EyeColor.HAZEL, new Identifier(Ref.MOD_ID, "textures/entity/cat/eyes/hazel.png"));
        enumMap.put(Genetics.EyeColor.GREEN, new Identifier(Ref.MOD_ID, "textures/entity/cat/eyes/green.png"));
        enumMap.put(Genetics.EyeColor.BLUE, new Identifier(Ref.MOD_ID, "textures/entity/cat/eyes/blue.png"));
    });

    public CatEyesFeatureRenderer(FeatureRendererContext<CatSCEntity, CatSCModel<CatSCEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CatSCEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (!entity.getEyeColor().isEmpty()) {
            Identifier eyeColorIdentifier = EYE_COLOR.get(Genetics.EyeColor.getByString(entity.getEyeColor()));
            if (eyeColorIdentifier != null && !entity.isInvisible()) {
                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(eyeColorIdentifier));
                (this.getContextModel()).render(matrices, vertexConsumer, light, LivingEntityRenderer.getOverlay(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
