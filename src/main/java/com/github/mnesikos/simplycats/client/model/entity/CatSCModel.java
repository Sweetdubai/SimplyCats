package com.github.mnesikos.simplycats.client.model.entity;

import com.github.mnesikos.simplycats.entity.CatSCEntity;
import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class CatSCModel<T extends CatSCEntity> extends AnimalModel<T> {
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart frightlegpoint;
    private final ModelPart fleftlegpoint;
    private final ModelPart brightlegpoint;
    private final ModelPart bleftlegpoint;
    private final ModelPart nose;
    private final ModelPart lear;
    private final ModelPart rear;
    private final ModelPart whiskers;
    private final ModelPart tail2;
    private final ModelPart frightleg;
    private final ModelPart fleftleg;
    private final ModelPart brightleg;
    private final ModelPart bleftleg;

    public CatSCModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;

        this.head = new ModelPart(this, 0, 0);
        this.head.setPivot(0.0F, 14.0F, -6.5F);
        this.head.addCuboid(-2.5F, -2.0F, -5.0F, 5, 4, 5, 0.0F);
        this.nose = new ModelPart(this, 21, 0);
        this.nose.setPivot(0.0F, 1.01F, -4.5F);
        this.nose.addCuboid(-1.5F, -1.0F, -2.0F, 3, 2, 2, 0.0F);
        this.whiskers = new ModelPart(this, 2, 9);
        this.whiskers.setPivot(0.0F, -0.1F, -1.2F);
        this.whiskers.addCuboid(-4.0F, -1.0F, -0.5F, 8, 2, 0, 0.0F);
        this.lear = new ModelPart(this, 26, 9);
        this.lear.setPivot(1.6F, -1.5F, -0.8F);
        this.lear.addCuboid(-1.0F, -2.0F, -1.0F, 2, 3, 2, 0.0F);
        this.rear = new ModelPart(this, 18, 9);
        this.rear.setPivot(-1.6F, -1.5F, -0.8F);
        this.rear.addCuboid(-1.0F, -2.0F, -1.0F, 2, 3, 2, 0.0F);
        this.body = new ModelPart(this, 20, 0);
        this.body.setPivot(0.0F, 18.0F, -5.0F);
        this.body.addCuboid(-2.5F, -4.0F, -1.5F, 5, 5, 15, 0.0F);
        this.fleftlegpoint = new ModelPart(this, 1, 11);
        this.fleftlegpoint.setPivot(2.2F, -0.5F, -0.7F);
        this.fleftlegpoint.addCuboid(-1.5F, 0.0F, -1.0F, 0, 0, 0, 0.0F);
        this.fleftleg = new ModelPart(this, 1, 11);
        this.fleftleg.setPivot(0.0F, -1.5F, 0.0F);
        this.fleftleg.addCuboid(-1.5F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
        this.frightlegpoint = new ModelPart(this, 9, 11);
        this.frightlegpoint.setPivot(-1.2F, -0.5F, -0.7F);
        this.frightlegpoint.addCuboid(-1.5F, 0.0F, -1.0F, 0, 0, 0, 0.0F);
        this.frightleg = new ModelPart(this, 9, 11);
        this.frightleg.setPivot(0.0F, -1.5F, 0.0F);
        this.frightleg.addCuboid(-1.5F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
        this.bleftlegpoint = new ModelPart(this, 1, 22);
        this.bleftlegpoint.setPivot(1.7F, -0.5F, 11.5F);
        this.bleftlegpoint.addCuboid(-1.0F, -1.5F, -1.0F, 0, 0, 0, 0.0F);
        this.bleftleg = new ModelPart(this, 1, 22);
        this.bleftleg.setPivot(0.0F, -1.5F, 0.0F);
        this.bleftleg.addCuboid(-1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
        this.brightlegpoint = new ModelPart(this, 9, 22);
        this.brightlegpoint.setPivot(-1.7F, -0.5F, 11.5F);
        this.brightlegpoint.addCuboid(-1.0F, -1.5F, -1.0F, 0, 0, 0, 0.0F);
        this.brightleg = new ModelPart(this, 9, 22);
        this.brightleg.setPivot(0.0F, -1.5F, 0.0F);
        this.brightleg.addCuboid(-1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
        this.tail = new ModelPart(this, 20, 22);
        this.tail.setPivot(0.0F, 15.0F, 7.6F);
        this.tail.addCuboid(-1.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
        this.setRotateAngle(tail, (float) (180 / (180 / Math.PI)), -0.0F, 0.0F);
        this.tail2 = new ModelPart(this, 28, 22);
        this.tail2.setPivot(0.0F, 7.8F, 0.0F);
        this.tail2.addCuboid(-1.0F, 0.0F, -1.0F, 2, 3, 2, 0.0F);
        this.setRotateAngle(tail2, (float) (10 / (180 / Math.PI)), -0.0F, 0.0F);

        this.head.addChild(this.nose); this.nose.addChild(this.whiskers);
        this.head.addChild(this.lear);
        this.head.addChild(this.rear);

        this.body.addChild(this.fleftlegpoint); this.fleftlegpoint.addChild(this.fleftleg);
        this.body.addChild(this.frightlegpoint); this.frightlegpoint.addChild(this.frightleg);
        this.body.addChild(this.bleftlegpoint); this.bleftlegpoint.addChild(this.bleftleg);
        this.body.addChild(this.brightlegpoint); this.brightlegpoint.addChild(this.brightleg);

        this.tail.addChild(this.tail2);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.body);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        float scale = 0.625F;
        if (this.child) {
            float var8 = 2.0F;
            matrices.push();
            matrices.scale(1.25F / var8, 1.25F / var8, 1.25F / var8);
            matrices.translate(0.0F, 15.6F * scale, 2.5F * scale);
            this.head.render(matrices, vertices, light, overlay);
            matrices.pop();

            matrices.push();
            matrices.scale(0.7F / var8, 0.7F / var8, 0.7F / var8);
            matrices.translate(0.0F, 41.0F * scale, 1.4F * scale);
            this.tail.render(matrices, vertices, light, overlay);
            matrices.pop();

            matrices.push();
            matrices.scale(1.0F / var8, 1.0F / var8, 0.8F / var8);
            matrices.translate(0.0F, 24.0F * scale, 0.0F);
            this.body.render(matrices, vertices, light, overlay);
            matrices.pop();

        } else {
            matrices.push();
            matrices.scale(1.01F, 1.01F, 1.01F);
            this.head.render(matrices, vertices, light, overlay);
            matrices.pop();
            this.body.render(matrices, vertices, light, overlay);
            this.tail.render(matrices, vertices, light, overlay);
        }
    }

    @Override
    public void setAngles(CatSCEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.head.pitch = headPitch / (180F / (float)Math.PI);
        this.head.yaw = headYaw / (180F / (float)Math.PI);

        /*if (entity instanceof EntityCat) {
            EntityCat cat = (EntityCat) entity;

            this.head.rotationPointY = 14.0F;
            this.head.rotationPointZ = -6.5F;
            this.body.rotateAngleX = 0.0F;
            this.body.rotationPointY = 18.0F;
            this.fleftlegpoint.rotateAngleX = this.frightlegpoint.rotateAngleX = 0.0F;
            this.bleftlegpoint.rotateAngleX = this.brightlegpoint.rotateAngleX = 0.0F;
            this.bleftlegpoint.rotationPointY = this.brightlegpoint.rotationPointY = -0.5F;
            this.fleftlegpoint.rotationPointY = this.frightlegpoint.rotationPointY = -0.5F;
            this.fleftleg.rotateAngleX = MathHelper.cos(parSpeed * 0.6662F) * 0.5F * parWalkSpeed;
            this.brightleg.rotateAngleX = MathHelper.cos(parSpeed * 0.6662F + 1.5F) * 0.5F * parWalkSpeed;
            this.frightleg.rotateAngleX = MathHelper.cos(parSpeed * 0.6662F + 3.0F) * 0.5F * parWalkSpeed;
            this.bleftleg.rotateAngleX = MathHelper.cos(parSpeed * 0.6662F + 4.5F) * 0.5F * parWalkSpeed;
            this.tail.rotationPointY = 15.0F;
            this.tail.rotateAngleX = (float) (180 / (180 / Math.PI));
            this.tail2.rotateAngleX = (float) (10 / (180 / Math.PI));
            this.lear.rotateAngleX = 0.0F;
            this.lear.rotateAngleY = 0.0F;
            this.rear.rotateAngleX = 0.0F;
            this.rear.rotateAngleY = 0.0F;

            if (cat.isAngry() || cat.isSneaking()) {
                this.lear.rotateAngleX = (float) (67 / (180 / Math.PI));
                this.lear.rotateAngleY = (float) (-145 / (180 / Math.PI));
                this.rear.rotateAngleX = (float) (67 / (180 / Math.PI));
                this.rear.rotateAngleY = (float) (145 / (180 / Math.PI));
            }

            if (cat.isSneaking()) {
                this.head.rotationPointY += 2.5F;
                this.body.rotationPointY += 2.0F;
                this.bleftlegpoint.rotationPointY -= 2.0F;
                this.brightlegpoint.rotationPointY -= 2.0F;
                this.fleftlegpoint.rotationPointY -= 2.0F;
                this.frightlegpoint.rotationPointY -= 2.0F;
                this.tail.rotationPointY += 2.0F;
                this.tail.rotateAngleX = ((float) Math.PI / 3F);
            }

            if (cat.isSitting()) {
                if (this.isChild) {
                    this.tail.rotationPointY = 23.5F;
                } else {
                    this.tail.rotationPointY = 21.5F;
                }
                this.head.rotationPointZ = -4.5F;
                this.body.rotateAngleX = (float) (-28 / (180 / Math.PI));
                this.fleftleg.rotateAngleX = this.frightleg.rotateAngleX = (float) (28 / (180 / Math.PI));
                this.bleftlegpoint.rotateAngleX = this.brightlegpoint.rotateAngleX = (float) (-62.5 / (180 / Math.PI));
                this.bleftleg.rotateAngleX = this.brightleg.rotateAngleX = 0.0F;
                this.tail.rotateAngleX = (float) (79 / (180 / Math.PI));

            }
        }*/
    }

    private void setRotateAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.pitch = x;
        modelRenderer.yaw = y;
        modelRenderer.roll = z;
    }
}
