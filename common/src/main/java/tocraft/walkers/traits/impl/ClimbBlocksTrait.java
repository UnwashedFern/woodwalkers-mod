package tocraft.walkers.traits.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.Walkers;
import tocraft.walkers.traits.ShapeTrait;

import java.util.ArrayList;
import java.util.List;

public class ClimbBlocksTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final ResourceLocation ID = Walkers.id("climb_blocks");
    public static final MapCodec<ClimbBlocksTrait<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.BOOL.optionalFieldOf("horizontal_collision", true).forGetter(o -> o.horizontalCollision),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("valid_blocks", new ArrayList<>()).forGetter(o -> o.validBlocks.stream().map(BuiltInRegistries.BLOCK::getKey).toList()),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("invalid_blocks", new ArrayList<>()).forGetter(o -> o.invalidBlocks.stream().map(BuiltInRegistries.BLOCK::getKey).toList())
    ).apply(instance, instance.stable((horizontalCollision, validBlocksLocation, invalidBlocksLocation) -> {
        List<Block> validBlocks = new ArrayList<>();
        for (ResourceLocation resourceLocation : validBlocksLocation) {
            if (BuiltInRegistries.BLOCK.containsKey(resourceLocation)) {
                validBlocks.add(BuiltInRegistries.BLOCK.get(resourceLocation).orElseThrow().value());
            }
        }
        List<Block> invalidBlocks = new ArrayList<>();
        for (ResourceLocation resourceLocation : invalidBlocksLocation) {
            if (BuiltInRegistries.BLOCK.containsKey(resourceLocation)) {
                validBlocks.add(BuiltInRegistries.BLOCK.get(resourceLocation).orElseThrow().value());
            }
        }
        return new ClimbBlocksTrait<>(horizontalCollision, validBlocks, invalidBlocks);
    })));

    public final boolean horizontalCollision;
    public final List<Block> validBlocks;
    public final List<Block> invalidBlocks;

    public ClimbBlocksTrait() {
        this(true);
    }

    public ClimbBlocksTrait(boolean horizontalCollision) {
        this(horizontalCollision, new ArrayList<>(), new ArrayList<>());
    }

    public ClimbBlocksTrait(List<Block> validBlocks, List<Block> invalidBlocks) {
        this(false, validBlocks, invalidBlocks);

    }

    public ClimbBlocksTrait(boolean horizontalCollision, List<Block> validBlocks, List<Block> invalidBlocks) {
        this.horizontalCollision = horizontalCollision;
        this.validBlocks = validBlocks;
        this.invalidBlocks = invalidBlocks;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends ShapeTrait<?>> codec() {
        return CODEC;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public @Nullable TextureAtlasSprite getIcon() {
        return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(Blocks.VINE.defaultBlockState()).particleIcon();
    }
}
