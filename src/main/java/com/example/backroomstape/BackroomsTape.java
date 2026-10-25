package com.example.backroomstape;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod("backrooms_tape")
public class BackroomsTape {
   public static final String MODID = "backrooms_tape";
   public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("backrooms_tape");
   public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("backrooms_tape");
   public static final DeferredRegister<CreativeModeTab> TABS;
   public static final DeferredBlock<CautionTapeBlock> CAUTION_TAPE;
   public static final DeferredItem<BlockItem> CAUTION_TAPE_ITEM;
   public static final DeferredBlock<CautionTapeBlock> NORMAL_TAPE;
   public static final DeferredItem<BlockItem> NORMAL_TAPE_ITEM;
   public static final DeferredBlock<DirectionalCautionTapeBlock> DIRECTIONAL_CAUTION_TAPE;
   public static final DeferredItem<BlockItem> DIRECTIONAL_CAUTION_TAPE_ITEM;
   public static final DeferredBlock<CautionTapeBlock> GLOW_CAUTION_TAPE;
   public static final DeferredItem<BlockItem> GLOW_CAUTION_TAPE_ITEM;
   public static final DeferredBlock<TripwireCautionTapeBlock> TRIPWIRE_CAUTION_TAPE;
   public static final DeferredItem<BlockItem> TRIPWIRE_CAUTION_TAPE_ITEM;
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB;

   public BackroomsTape(IEventBus modEventBus) {
      BLOCKS.register(modEventBus);
      ITEMS.register(modEventBus);
      TABS.register(modEventBus);
   }

   static {
      TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "backrooms_tape");
      CAUTION_TAPE = BLOCKS.register("caution_tape", () -> new CautionTapeBlock(Properties.of().mapColor(MapColor.COLOR_YELLOW).noCollission().instabreak().sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY)));
      CAUTION_TAPE_ITEM = ITEMS.registerSimpleBlockItem(CAUTION_TAPE);
      NORMAL_TAPE = BLOCKS.register("normal_tape", () -> new CautionTapeBlock(Properties.of().mapColor(MapColor.COLOR_YELLOW).noCollission().instabreak().sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY)));
      NORMAL_TAPE_ITEM = ITEMS.registerSimpleBlockItem(NORMAL_TAPE);
      DIRECTIONAL_CAUTION_TAPE = BLOCKS.register("directional_caution_tape", () -> new DirectionalCautionTapeBlock(Properties.of().mapColor(MapColor.COLOR_YELLOW).noCollission().instabreak().sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY)));
      DIRECTIONAL_CAUTION_TAPE_ITEM = ITEMS.registerSimpleBlockItem(DIRECTIONAL_CAUTION_TAPE);
      GLOW_CAUTION_TAPE = BLOCKS.register("glow_caution_tape", () -> new CautionTapeBlock(Properties.of().mapColor(MapColor.COLOR_YELLOW).noCollission().instabreak().sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY).lightLevel(state -> 9)));
      GLOW_CAUTION_TAPE_ITEM = ITEMS.registerSimpleBlockItem(GLOW_CAUTION_TAPE);
      TRIPWIRE_CAUTION_TAPE = BLOCKS.register("tripwire_caution_tape", () -> new TripwireCautionTapeBlock(Properties.of().mapColor(MapColor.COLOR_YELLOW).noCollission().instabreak().sound(SoundType.WOOL).pushReaction(PushReaction.DESTROY)));
      TRIPWIRE_CAUTION_TAPE_ITEM = ITEMS.registerSimpleBlockItem(TRIPWIRE_CAUTION_TAPE);
      TAB = TABS.register("main", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.backrooms_tape")).icon(() -> new ItemStack((ItemLike)CAUTION_TAPE_ITEM.get())).displayItems((params, output) -> {
         output.accept((ItemLike)CAUTION_TAPE_ITEM.get());
         output.accept((ItemLike)NORMAL_TAPE_ITEM.get());
         output.accept((ItemLike)DIRECTIONAL_CAUTION_TAPE_ITEM.get());
         output.accept((ItemLike)GLOW_CAUTION_TAPE_ITEM.get());
         output.accept((ItemLike)TRIPWIRE_CAUTION_TAPE_ITEM.get());
      }).build());
   }
}
