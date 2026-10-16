package com.realcameracompat.client.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.realcameracompat.client.RealCameraCompatClient;
import com.xtracr.realcamera.renderer.MultiVertexCatcher;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.state.ETFState;
import traben.entity_texture_features.features.state.ETFSubmitData;
import traben.entity_texture_features.features.state.HoldsETFRenderState;

@Mixin(value = MultiVertexCatcher.class, remap = false)
public class MixinMultiVertexCatcher {
	private static int rcc$mountDepth;

	@Inject(method = "renderModelSubmit", at = @At("HEAD"))
	private static void rcc$beforeRenderModelSubmit(ModelFeatureRenderer.Submit<?> submit, VertexConsumer buffer, CallbackInfo ci) {
		try {
			FabricLoader loader = FabricLoader.getInstance();
			if (!loader.isModLoaded("entity_model_features") || !loader.isModLoaded("entity_texture_features")) {
				return;
			}
			ETFSubmitData data = ETFSubmitData.from(submit);
			ETFEntityRenderState state = null;
			if (submit.state() instanceof HoldsETFRenderState holds) {
				state = holds.etf$getState();
			}
			if (state == null && data != null) {
				state = data.backupState;
			}
			if (state == null) {
				ETFState.mountNone();
			} else {
				state.preSubmitActivate(data, submit);
				ETFState.mount(state);
			}
			rcc$mountDepth++;
		} catch (Throwable t) {
			RealCameraCompatClient.LOGGER.warn("Real Camera Compat: falha ao ativar o estado EMF/ETF na captura de modelo", t);
		}
	}

	@Inject(method = "renderModelSubmit", at = @At("RETURN"))
	private static void rcc$afterRenderModelSubmit(ModelFeatureRenderer.Submit<?> submit, VertexConsumer buffer, CallbackInfo ci) {
		try {
			if (rcc$mountDepth <= 0) {
				return;
			}
			rcc$mountDepth--;
			ETFState.unMount();
		} catch (Throwable t) {
			RealCameraCompatClient.LOGGER.warn("Real Camera Compat: falha ao desativar o estado EMF/ETF apos a captura de modelo", t);
		}
	}
}
