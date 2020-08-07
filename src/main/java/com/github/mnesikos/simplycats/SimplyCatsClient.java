package com.github.mnesikos.simplycats;

import com.github.mnesikos.simplycats.client.render.entity.CatSCEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

import static com.github.mnesikos.simplycats.SimplyCats.CAT;

@Environment(EnvType.CLIENT)
public class SimplyCatsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(CAT, ((entityRenderDispatcher, context) -> new CatSCEntityRenderer(entityRenderDispatcher)));
    }
}
