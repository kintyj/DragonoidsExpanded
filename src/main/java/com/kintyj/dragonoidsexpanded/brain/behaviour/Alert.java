package com.kintyj.dragonoidsexpanded.brain.behaviour;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import com.kintyj.dragonoidsexpanded.entity.IAlertable;
import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.ToFloatBiFunction;
import net.tslat.smartbrainlib.util.BrainUtil;

public class Alert<E extends LivingEntity> extends ExtendedBehaviour<E> {
    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
        Pair.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryStatus.REGISTERED),
        Pair.of(MemoryModuleType.DUMMY, MemoryStatus.VALUE_ABSENT));

    protected Predicate<LivingEntity> alertPredicate = target -> false;
    protected Predicate<? super Entity> canInformPredicate = target -> false;
    protected ToFloatBiFunction<E, LivingEntity> noCloserThanSqrSupplier = (entity, target) -> 9.0f;
    protected ToIntFunction<E> cooldownSupplier = (entity) -> 120;

    protected Predicate<? super Entity> canInformInternalPredicate = target -> target instanceof IAlertable ? canInformPredicate.test(target) : false;

    protected Optional<LivingEntity> target = Optional.empty();

    public Alert() {
        super();
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    public Alert<E> alert(Predicate<LivingEntity> predicate) {
        this.alertPredicate = predicate;

        return this;
    }

    public Alert<E> canInform(Predicate<? super Entity> predicate) {
        this.canInformPredicate = predicate;

        return this;
    }

    public Alert<E> noCloserThanSqr(ToFloatBiFunction<E, LivingEntity> supplier) {
        this.noCloserThanSqrSupplier = supplier;

        return this;
    }

    public Alert<E> cooldown(ToIntFunction<E> supplier) {
        this.cooldownSupplier = supplier;

        return this;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        this.target = BrainUtil.getMemory(entity, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).findClosest(this.alertPredicate);

        if (this.target.isEmpty())
            return false;

        LivingEntity avoidingEntity = target.get();
        double distToTarget = avoidingEntity.distanceToSqr(entity);

        if (distToTarget > this.noCloserThanSqrSupplier.applyAsFloat(entity, avoidingEntity))
            return false;

        return true;
    }

    @Override
    protected void start(E entity) {
        Iterable<LivingEntity> nearby = BrainUtil.getMemory(entity, MemoryModuleType.NEAREST_LIVING_ENTITIES).stream().filter(this.canInformInternalPredicate).collect(Collectors.toList());

        for (LivingEntity alertable : nearby) {
            ((IAlertable)alertable).onAlert(entity, this.target.get());
        }

        BrainUtil.setForgettableMemory(entity, MemoryModuleType.DUMMY, null, cooldownSupplier.applyAsInt(entity));
    }
}
