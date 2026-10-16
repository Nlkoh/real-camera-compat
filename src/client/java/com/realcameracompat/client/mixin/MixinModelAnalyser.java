package com.realcameracompat.client.mixin;

import com.realcameracompat.client.compat.SkinLayersUvFilter;
import com.xtracr.realcamera.gui.ModelAnalyser;
import com.xtracr.realcamera.renderer.state.BuiltModelRecord;
import com.xtracr.realcamera.renderer.state.VertexData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ModelAnalyser.class, remap = false)
public class MixinModelAnalyser {
	@Redirect(
			method = "computeFocusedOnModel",
			at = @At(
					value = "INVOKE",
					target = "Lcom/xtracr/realcamera/renderer/state/BuiltModelRecord;primitives()[[Lcom/xtracr/realcamera/renderer/state/VertexData;"
			)
	)
	private VertexData[][] rcc$filterSkinLayersPrimitives(BuiltModelRecord record) {
		try {
			return SkinLayersUvFilter.filterPrimitives(record);
		} catch (Throwable t) {
			return record.primitives();
		}
	}
}
