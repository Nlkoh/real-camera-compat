package com.realcameracompat.client.compat;

import com.realcameracompat.client.RealCameraCompatClient;
import com.xtracr.realcamera.RealCameraCore;
import dev.tr7zw.firstperson.api.ActivationHandler;
import dev.tr7zw.firstperson.api.FirstPersonAPI;

public final class FirstPersonModelBridge {
	private FirstPersonModelBridge() {
	}

	public static void register() {
		FirstPersonAPI.registerPlayerHandler((ActivationHandler) RealCameraCore::isActive);
		RealCameraCompatClient.LOGGER.info("Compatibilidade com First Person Model ativada (corpo escondido enquanto a camera esta em uso)");
	}
}
