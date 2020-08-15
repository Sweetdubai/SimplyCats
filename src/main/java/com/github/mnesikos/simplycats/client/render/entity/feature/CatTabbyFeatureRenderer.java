package com.github.mnesikos.simplycats.client.render.entity.feature;

import com.github.mnesikos.simplycats.Ref;
import com.github.mnesikos.simplycats.client.model.entity.CatSCModel;
import com.github.mnesikos.simplycats.entity.CatSCEntity;
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

import java.util.Map;

@Environment(EnvType.CLIENT)
public class CatTabbyFeatureRenderer extends FeatureRenderer<CatSCEntity, CatSCModel<CatSCEntity>> {
    public static final Map<String, Identifier> TABBY = Maps.newHashMap();

    public CatTabbyFeatureRenderer(FeatureRendererContext<CatSCEntity, CatSCModel<CatSCEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CatSCEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        String tabby = entity.getTabbyTexture();
        if (!tabby.isEmpty()) {
            Identifier tabbyIdentifier = TABBY.get(tabby);

            if (tabbyIdentifier == null) {
                tabbyIdentifier = new Identifier(Ref.MOD_ID + ":textures/entity/cat/tabby/" + tabby + ".png");
                TABBY.put(tabby, tabbyIdentifier);
            }

            if (!entity.isInvisible()) {
                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(tabbyIdentifier));
                (this.getContextModel()).render(matrices, vertexConsumer, light, LivingEntityRenderer.getOverlay(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
