package com.example.backroomstape;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class DirectionalCautionTapeBlock extends CautionTapeBlock {
   public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);

   public DirectionalCautionTapeBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(NORTH, EAST, SOUTH, WEST, COLOR, FACING);
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      BlockState state = super.getStateForPlacement(ctx);
      return state.setValue(FACING, ctx.getHorizontalDirection());
   }
}
