package com.github.mnesikos.simplycats;

import com.github.mnesikos.simplycats.entity.CatSCEntity;
import com.github.mnesikos.simplycats.init.ModBlocks;
import com.github.mnesikos.simplycats.init.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SimplyCats implements ModInitializer {
	public static final ItemGroup GROUP = FabricItemGroupBuilder.build(
			new Identifier("simplycats", "tab"),
			() -> new ItemStack(ModItems.CATNIP));

	public static final EntityType<CatSCEntity> CAT = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier(Ref.MOD_ID, "cat"),
			FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CatSCEntity::new).dimensions(EntityDimensions.fixed(0.6f, 0.8f)).trackable(80, 1, true).build()
	);

	@Override
	public void onInitialize() {
		ModItems.register();
		ModBlocks.register();
		FabricDefaultAttributeRegistry.register(CAT, CatSCEntity.createCatAttributes());
	}
}
