package com.realcameracompat.client;

import com.realcameracompat.client.compat.FirstPersonModelBridge;
import com.realcameracompat.client.compat.SkinLayersUvFilter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RealCameraCompatClient implements ClientModInitializer {
	public static final String MOD_ID = "realcameracompat";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		FabricLoader loader = FabricLoader.getInstance();
		boolean realCamera = loader.isModLoaded("realcamera");
		boolean emf = loader.isModLoaded("entity_model_features");
		boolean etf = loader.isModLoaded("entity_texture_features");
		boolean firstPersonModel = loader.isModLoaded("firstperson");
		boolean skinLayers3d = loader.isModLoaded("skinlayers3d");

		if (firstPersonModel) {
			FirstPersonModelBridge.register();
		}
		SkinLayersUvFilter.init();

		LOGGER.info("Real Camera Compat iniciado (Real Camera: {}, EMF: {}, ETF: {}, FPM: {}, 3DSL: {})", realCamera, emf, etf, firstPersonModel, skinLayers3d);
	}
}
