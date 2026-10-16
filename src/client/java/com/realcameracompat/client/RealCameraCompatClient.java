package com.realcameracompat.client;

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

		LOGGER.info("Real Camera Compat iniciado (Real Camera: {}, EMF: {}, ETF: {})", realCamera, emf, etf);
	}
}
