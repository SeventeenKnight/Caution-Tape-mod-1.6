package com.example.backroomstape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.SoundType;
import org.joml.Vector3f;

public class TripwireCautionTapeBlock extends CautionTapeBlock {
   public static final BooleanProperty POWERED = BooleanProperty.create("powered");

   public TripwireCautionTapeBlock(BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(POWERED, false));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{POWERED});
   }

   @Override
   public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      if (level.isClientSide || state.getValue(POWERED)) {
         return;
      }

      level.setBlock(pos, state.setValue(POWERED, true), 3);
      level.scheduleTick(pos, this, 12);
      level.playSound(null, pos, SoundType.WOOL.getPlaceSound(), SoundSource.BLOCKS, 1.0F, 0.8F);
      if (level instanceof ServerLevel serverLevel) {
         Vector3f warningYellow = new Vector3f(1.0F, 0.82F, 0.1F);
         serverLevel.sendParticles(new DustParticleOptions(warningYellow, 1.2F), pos.getX() + 0.5, pos.getY() + 0.12, pos.getZ() + 0.5, 12, 0.3, 0.02, 0.3, 0.02);
      }
      if (entity instanceof LivingEntity livingEntity) {
         CautionTapeBlock.awardAdvancement(livingEntity, level, "caught_in_the_tape", "triggered_tripwire");
      }
   }

   @Override
   public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (state.getValue(POWERED)) {
         level.setBlock(pos, state.setValue(POWERED, false), 3);
      }
   }

   @Override
   public boolean isSignalSource(BlockState state) {
      return true;
   }

   @Override
   public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
      return state.getValue(POWERED) ? 15 : 0;
   }
}