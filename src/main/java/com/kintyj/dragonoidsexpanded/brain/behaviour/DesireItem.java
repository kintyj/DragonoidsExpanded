package com.kintyj.dragonoidsexpanded.brain.behaviour;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.item.ItemEntity;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.registry.SBLMemoryTypes;
import net.tslat.smartbrainlib.util.BrainUtil;

public class DesireItem<E extends LivingEntity> extends ExtendedBehaviour<E> {
    // The generic type E here represents the minimum entity type of the entities that could use this behaviour. Other examples might be Mob, or PathfinderMob

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED), Pair.of(SBLMemoryTypes.NEARBY_ITEMS.get(), MemoryStatus.VALUE_PRESENT));
    // We declare that the entity shouldn't have an attack target already, and that the NEAREST_VISIBLE_LIVING_ENTITIES memory has something before this behaviour can start

    protected Predicate<ItemEntity> targetPredicate = item -> true; // Predicate that determines an applicable target
    protected Predicate<E> canTargetPredicate = entity -> true; // Predicate that determines whether our entity is ready to target or not
    protected Predicate<E> success = entity -> true;
    protected Predicate<E> fail = entity -> true;
    protected BiConsumer<E, ItemEntity> hasItem = (entity, item) -> {};

    public DesireItem<E> hasItem(BiConsumer<E, ItemEntity> biConsumer) {
        this.hasItem = biConsumer;

        return this;
    }

    // Allow for setting the target predicate dynamically
    public DesireItem<E> targetPredicate(Predicate<ItemEntity> predicate) {
        this.targetPredicate = predicate;

        return this;
    }

    public DesireItem<E> success(Predicate<E> predicate) {
        this.success = predicate;

        return this;
    }

    public DesireItem<E> fail(Predicate<E> predicate) {
        this.fail = predicate;

        return this;
    }

    // Allow for setting the can attack predicate dynamically
    public DesireItem<E> canTargetPredicate(Predicate<E> predicate) {
        this.canTargetPredicate = predicate;

        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS; // Return our static memory requirements here. Can also be non-static if required, but that's less performant
    }

    @Override // Use our can target predicate here to see if we should run the behaviour
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        return this.canTargetPredicate.test(entity);
    }

    @Override // Actually handle the function of the behaviour here
    protected void start(E entity) {
        List<ItemEntity> wantedItems = BrainUtil.getMemory(entity, SBLMemoryTypes.NEARBY_ITEMS.get()); // Get the nearby entities memory

        if (wantedItems.size() <= 0) {
            BrainUtil.clearMemory(entity, MemoryModuleType.WALK_TARGET);
            fail.test(entity);
        } else {
            ItemEntity wantedItem = wantedItems.get(entity.getRandom().nextInt(wantedItems.size()));
            if (!targetPredicate.test(wantedItem)) {
                BrainUtil.clearMemory(entity, MemoryModuleType.WALK_TARGET);
                fail.test(entity);
            } else if (wantedItem.distanceTo(entity) < 1.0f) {
                BrainUtil.clearMemory(entity, MemoryModuleType.WALK_TARGET);
                hasItem.accept(entity, wantedItem);
            } else { // Target found, set the target in memory, and reset the unreachable target timer
                BrainUtil.setMemory(entity, MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityTracker(wantedItem, false), 1.0F, 1));
                BrainUtil.clearMemory(entity, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
                success.test(entity);
            }
        }
    }

    @Override
    protected void stop(E entity) {
        
        super.stop(entity);
    }
}