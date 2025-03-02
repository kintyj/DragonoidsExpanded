package com.kintyj.dragonoidsexpanded.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record WhipStateComponent(int state) {
    public static final Codec<WhipStateComponent> BASIC_CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("state").forGetter(WhipStateComponent::state)
        ).apply(instance, WhipStateComponent::new)
    );

    public static final StreamCodec<ByteBuf, WhipStateComponent> BASIC_STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, WhipStateComponent::state,
        WhipStateComponent::new
    );
}
