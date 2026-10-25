package com.example.backroomstape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.BlockHitResult;
import org.joml.Vector3f;

public class CautionTapeBlock extends Block {
   public static final BooleanProperty NORTH = BooleanProperty.create("north");
   public static final BooleanProperty EAST = BooleanProperty.create("east");
   public static final BooleanProperty SOUTH = BooleanProperty.create("south");
   public static final BooleanProperty WEST = BooleanProperty.create("west");
   public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);
   private static final VoxelShape DOT = Block.box((double)5.0F, (double)0.0F, (double)5.0F, (double)11.0F, (double)1.0F, (double)11.0F);
   private static final VoxelShape ARM_N = Block.box((double)5.0F, (double)0.0F, (double)0.0F, (double)11.0F, (double)1.0F, (double)5.0F);
   private static final VoxelShape ARM_E = Block.box((double)11.0F, (double)0.0F, (double)5.0F, (double)16.0F, (double)1.0F, (double)11.0F);
   private static final VoxelShape ARM_S = Block.box((double)5.0F, (double)0.0F, (double)11.0F, (double)11.0F, (double)1.0F, (double)16.0F);
   private static final VoxelShape ARM_W = Block.box((double)0.0F, (double)0.0F, (double)5.0F, (double)5.0F, (double)1.0F, (double)11.0F);
   private static final VoxelShape[] SHAPES = new VoxelShape[16];

   public CautionTapeBlock(BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(COLOR, DyeColor.YELLOW)).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false));
   }

   private static BooleanProperty property(Direction dir) {
      BooleanProperty var10000;
      switch (dir) {
         case NORTH -> var10000 = NORTH;
         case EAST -> var10000 = EAST;
         case SOUTH -> var10000 = SOUTH;
         case WEST -> var10000 = WEST;
         default -> throw new IllegalArgumentException("Nur horizontale Richtungen: " + String.valueOf(dir));
      }

      return var10000;
   }

   private static int index(BlockState state) {
      int i = 0;
      if ((Boolean)state.getValue(NORTH)) {
         i |= 1;
      }

      if ((Boolean)state.getValue(EAST)) {
         i |= 2;
      }

      if ((Boolean)state.getValue(SOUTH)) {
         i |= 4;
      }

      if ((Boolean)state.getValue(WEST)) {
         i |= 8;
      }

      return i;
   }

   private boolean connectsTo(BlockState neighbor) {
      return neighbor.getBlock() instanceof CautionTapeBlock;
   }

   @Override
   public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);
      if (stack.is(Items.POTION) && potionContents != null && potionContents.is(Potions.WATER)) {
         if (state.getValue(COLOR) == DyeColor.YELLOW) {
            return ItemInteractionResult.SUCCESS;
         }

         if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(COLOR, DyeColor.YELLOW), 3);
            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.BOTTLE_EMPTY, net.minecraft.sounds.SoundSource.BLOCKS, 0.8F, 1.0F);
            spawnColorParticles(level, pos, DyeColor.YELLOW);
            if (!player.getAbilities().instabuild) {
               stack.shrink(1);
               ItemStack glassBottle = new ItemStack(Items.GLASS_BOTTLE);
               if (stack.isEmpty()) {
                  player.setItemInHand(hand, glassBottle);
               } else if (!player.getInventory().add(glassBottle)) {
                  player.drop(glassBottle, false);
               }
            }
            awardAdvancement(player, level, "clean_sweep", "washed_tape");
         }

         return ItemInteractionResult.SUCCESS;
      }

      if (!(stack.getItem() instanceof DyeItem dyeItem)) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }

      DyeColor color = dyeItem.getDyeColor();
      if (state.getValue(COLOR) == color) {
         return ItemInteractionResult.SUCCESS;
      }

      if (!level.isClientSide) {
         level.setBlock(pos, state.setValue(COLOR, color), 3);
         if (!player.getAbilities().instabuild) {
            stack.shrink(1);
         }
         level.playSound(null, pos, SoundType.WOOL.getPlaceSound(), net.minecraft.sounds.SoundSource.BLOCKS, 0.8F, 1.25F);
         spawnColorParticles(level, pos, color);
         awardAdvancement(player, level, "fresh_coat", "dyed_tape");
      }

      return ItemInteractionResult.SUCCESS;
   }

   private static void spawnColorParticles(Level level, BlockPos pos, DyeColor color) {
      if (level instanceof ServerLevel serverLevel) {
         int rgb = color.getTextureDiffuseColor();
         Vector3f particleColor = new Vector3f(((rgb >> 16) & 255) / 255.0F, ((rgb >> 8) & 255) / 255.0F, (rgb & 255) / 255.0F);
         serverLevel.sendParticles(new DustParticleOptions(particleColor, 1.0F), pos.getX() + 0.5, pos.getY() + 0.08, pos.getZ() + 0.5, 14, 0.24, 0.02, 0.24, 0.01);
      }
   }

   @Override
   public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
      super.setPlacedBy(level, pos, state, placer, stack);
      awardAdvancement(placer, level, "root", "placed_tape");
      if (state.is(BackroomsTape.DIRECTIONAL_CAUTION_TAPE.get())) {
         awardAdvancement(placer, level, "follow_the_arrows", "placed_directional_tape");
      }
      if (state.is(BackroomsTape.GLOW_CAUTION_TAPE.get())) {
         awardAdvancement(placer, level, "light_the_way", "placed_glow_tape");
      }
   }

   static void awardAdvancement(LivingEntity player, Level level, String advancementName, String criterion) {
      if (!(player instanceof ServerPlayer serverPlayer)) {
         return;
      }

      MinecraftServer server = level.getServer();
      if (server == null) {
         return;
      }

      ResourceLocation id = ResourceLocation.fromNamespaceAndPath(BackroomsTape.MODID, advancementName);
      var advancement = server.getAdvancements().get(id);
      if (advancement != null) {
         serverPlayer.getAdvancements().award(advancement, criterion);
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(new Property[]{NORTH, EAST, SOUTH, WEST, COLOR});
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
      return SHAPES[index(state)];
   }

   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      LevelReader level = ctx.getLevel();
      BlockPos pos = ctx.getClickedPos();
      BlockState state = this.defaultBlockState();

      for(Direction dir : Plane.HORIZONTAL) {
         state = (BlockState)state.setValue(property(dir), this.connectsTo(level.getBlockState(pos.relative(dir))));
      }

      return state;
   }

   protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      BlockPos below = pos.below();
      return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
   }

   protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
      if (direction == Direction.DOWN) {
         return this.canSurvive(state, level, pos) ? state : Blocks.AIR.defaultBlockState();
      } else {
         return direction.getAxis().isHorizontal() ? (BlockState)state.setValue(property(direction), this.connectsTo(neighborState)) : state;
      }
   }

   static {
      for(int i = 0; i < 16; ++i) {
         VoxelShape shape = DOT;
         if ((i & 1) != 0) {
            shape = Shapes.or(shape, ARM_N);
         }

         if ((i & 2) != 0) {
            shape = Shapes.or(shape, ARM_E);
         }

         if ((i & 4) != 0) {
            shape = Shapes.or(shape, ARM_S);
         }

         if ((i & 8) != 0) {
            shape = Shapes.or(shape, ARM_W);
         }

         SHAPES[i] = shape;
      }

   }
}
