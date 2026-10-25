package com.example.backroomstape;

import net.minecraft.client.color.block.BlockColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = BackroomsTape.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class BackroomsTapeClient {
   private BackroomsTapeClient() {
   }

   @SubscribeEvent
   public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
      BlockColor tapeColor = (state, level, pos, tintIndex) -> state.getValue(CautionTapeBlock.COLOR).getTextureDiffuseColor();
      event.register(tapeColor, BackroomsTape.CAUTION_TAPE.get(), BackroomsTape.DIRECTIONAL_CAUTION_TAPE.get(), BackroomsTape.GLOW_CAUTION_TAPE.get(), BackroomsTape.TRIPWIRE_CAUTION_TAPE.get());
   }
}