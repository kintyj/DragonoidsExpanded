package com.kintyj.dragonoidsexpanded.item;

import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.kintyj.dragonoidsexpanded.client.renderer.item.ManticorePawRenderer;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ManticorePaw extends Item implements GeoItem {
    private static final RawAnimation POPUP_ANIM = RawAnimation.begin().thenPlay("use.popup");
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final int COOLDOWN_TIME = 1200;
    public static final int INVENTORY_TICK_SKIP = 35;
    public static final float INVENTORY_EFFECT_CHANCE = 0.25f;
    public static final float INVENTORY_ACTIVATION_CHANCE = 0.15f;

    public ManticorePaw(Properties properties) {
        super(properties);

		SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }
    
    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "manticore_paw", 20, state -> PlayState.STOP)
				.triggerableAnim("use", POPUP_ANIM));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // Create our armor model/renderer and return it
	@Override
	public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
		consumer.accept(new GeoRenderProvider() {
			private ManticorePawRenderer renderer;

			@Override
			@Nullable
			public GeoItemRenderer<ManticorePaw> getGeoItemRenderer() {
				if (this.renderer == null)
					this.renderer = new ManticorePawRenderer();
				// Defer creation of our renderer then cache it so that it doesn't get instantiated too early

				return this.renderer;
			}
		});
	}


    public void activate(@Nonnull Level level, @Nonnull Entity target, @Nonnull ItemStack stack) {
        if (level instanceof ServerLevel serverLevel) {
            triggerAnim(target, GeoItem.getOrAssignId(stack, serverLevel), "manticore_paw", "use");
            

            switch (level.random.nextInt(5)) {
                case 0:
                    target.kill(serverLevel);
                    target.playSound(SoundEvents.DECORATED_POT_SHATTER, 1, 1.5f);
                    break;
                case 1:
                    MobEffectInstance weaknessInstance = new MobEffectInstance(MobEffects.WEAKNESS, 300, 8);
                    if (target instanceof Mob mob) {
                        mob.addEffect(weaknessInstance);
                    } else if (target instanceof Player player) {
                        player.addEffect(weaknessInstance);
                    }
                    target.playSound(SoundEvents.DECORATED_POT_SHATTER, 1, 1);
                    break;
                case 2:
                    target.setAirSupply(0);
                    target.playSound(SoundEvents.DROWNED_AMBIENT, 1, 1);
                    break;
                case 3:
                    if (target instanceof Mob mob) {
                        mob.setHealth(mob.getHealth() / 2);
                    } else if (target instanceof Player player) {
                        player.setHealth(player.getHealth() / 2);
                    }
                    target.playSound(SoundEvents.WARDEN_AGITATED, 1, 0.25f);
                    break;
                case 4:
                    target.extinguishFire();
                    if (target instanceof Mob mob) {
                        mob.heal(mob.getMaxHealth());
                    } else if (target instanceof Player player) {
                        player.heal(player.getMaxHealth());
                    }
                    target.playSound(SoundEvents.ZOMBIE_INFECT, 1, 1.75f);
                    break;
                default:
                    target.playSound(SoundEvents.EVOKER_PREPARE_WOLOLO, 1, 1);
                    break;
            }
        }
    }

    @Override
    public void inventoryTick(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull Entity entity, int slotId, boolean isSelected) {
        if (getTick(stack) % INVENTORY_TICK_SKIP == 0) {
            if (level.random.nextFloat() < INVENTORY_EFFECT_CHANCE) {
                if (level.random.nextFloat() < INVENTORY_ACTIVATION_CHANCE) {
                    activate(level, entity, stack);
                } else {
                    entity.playSound(SoundEvents.AMBIENT_CAVE.value());
                }
            } 
        }
    }

    @Override
    public InteractionResult use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!player.getCooldowns().isOnCooldown(itemstack)) {
            activate(level, player, itemstack);
            player.getCooldowns().addCooldown(itemstack, COOLDOWN_TIME);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
