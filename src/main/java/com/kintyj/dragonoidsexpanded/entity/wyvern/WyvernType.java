package com.kintyj.dragonoidsexpanded.entity.wyvern;

import com.kintyj.dragonoidsexpanded.DragonoidsExpanded;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ExtraCodecs;

public class WyvernType {
    public String name;
    public float scale;
    public int hp;
    public int attack;

    public WyvernType(String name, float scale, int hp, int attack) {
        this.name = name;
        this.scale = scale;
        this.hp = hp;
        this.attack = attack;

        // Minecraft.getInstance().level.registryAccess().registry(Registries.ENTITY_TYPE).get().getTag(DragonoidsExpanded.DRAGONOID_TAG_KEY).stream().anyMatch(holder
        // -> holder.is());
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public float getScale() {
        return scale;
    }

    public int getHP() {
        return hp;
    }

    public int getAttack() {
        return attack;
    }

    public static final class Serializer {
        public Serializer() {
        }

        public static MapCodec<WyvernType> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(Codec.STRING.fieldOf("name").forGetter((wyvernType) -> {
                return wyvernType.name;
            }), Codec.FLOAT.fieldOf("scale").forGetter((wyvernType) -> {
                return wyvernType.scale;
            }), ExtraCodecs.POSITIVE_INT.fieldOf("hp").forGetter((wyvernType) -> {
                return wyvernType.hp;
            }), ExtraCodecs.POSITIVE_INT.fieldOf("attack").forGetter((wyvernType) -> {
                return wyvernType.attack;
            })).apply(instance, WyvernType::new);
        });

        /*
         * public static StreamCodec<RegistryFriendlyByteBuf, WyvernType> STREAM_CODEC =
         * StreamCodec
         * .of(Serializer::write, Serializer::read);
         * 
         * private static WyvernType read(RegistryFriendlyByteBuf buffer) {
         * String name = buffer.readUtf();
         * float scale = buffer.readFloat();
         * int hp = buffer.readInt();
         * int attack = buffer.readInt();
         * 
         * return new WyvernType(name, scale, hp, attack);
         * }
         * 
         * private static void write(RegistryFriendlyByteBuf buffer, WyvernType
         * wyvernType) {
         * buffer.writeUtf(wyvernType.name);
         * buffer.writeFloat(wyvernType.scale);
         * buffer.writeInt(wyvernType.hp);
         * buffer.writeInt(wyvernType.attack);
         * }
         */
    }
}