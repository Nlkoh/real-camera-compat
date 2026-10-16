package com.realcameracompat.client.compat;

import com.realcameracompat.client.RealCameraCompatClient;
import com.xtracr.realcamera.renderer.state.BuiltModelRecord;
import com.xtracr.realcamera.renderer.state.VertexData;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class SkinLayersUvFilter {
	private static final Set<Long> THREE_DEE_UVS = ConcurrentHashMap.newKeySet();
	private static boolean enabled;
	private static boolean reflectionReady;
	private static boolean warned;
	private static Method getInjectedMesh;
	private static Field polygonData;
	private static Field polygonAmount;

	private SkinLayersUvFilter() {
	}

	public static void init() {
		try {
			enabled = FabricLoader.getInstance().isModLoaded("skinlayers3d");
		} catch (Throwable t) {
			enabled = false;
		}
	}

	public static void beginCapture() {
		if (!enabled) {
			return;
		}
		try {
			THREE_DEE_UVS.clear();
		} catch (Throwable ignored) {
		}
	}

	public static void collectFromModel(Model<?> model) {
		if (!enabled || model == null) {
			return;
		}
		try {
			if (!setupReflection()) {
				return;
			}
			for (ModelPart part : model.root().getAllParts()) {
				Object mesh = getInjectedMesh.invoke(part);
				if (mesh != null) {
					collectMeshUvs(mesh);
				}
			}
		} catch (Throwable t) {
			warnOnce(t);
		}
	}

	public static VertexData[][] filterPrimitives(BuiltModelRecord record) {
		VertexData[][] original = record.primitives();
		if (!enabled || THREE_DEE_UVS.isEmpty() || original == null || original.length == 0) {
			return original;
		}
		try {
			List<VertexData[]> kept = new ArrayList<>(original.length);
			for (VertexData[] primitive : original) {
				if (!isSkinLayersPrimitive(primitive)) {
					kept.add(primitive);
				}
			}
			if (kept.size() == original.length) {
				return original;
			}
			return kept.toArray(new VertexData[0][]);
		} catch (Throwable t) {
			warnOnce(t);
			return original;
		}
	}

	private static boolean setupReflection() {
		if (reflectionReady) {
			return getInjectedMesh != null;
		}
		reflectionReady = true;
		try {
			Class<?> injectorClass = Class.forName("dev.tr7zw.skinlayers.accessor.ModelPartInjector");
			getInjectedMesh = injectorClass.getMethod("getInjectedMesh");
			Class<?> customPartClass = Class.forName("dev.tr7zw.skinlayers.versionless.render.CustomModelPart");
			polygonData = customPartClass.getDeclaredField("polygonData");
			polygonData.setAccessible(true);
			polygonAmount = customPartClass.getDeclaredField("polygonAmount");
			polygonAmount.setAccessible(true);
			return true;
		} catch (Throwable t) {
			getInjectedMesh = null;
			polygonData = null;
			polygonAmount = null;
			warnOnce(t);
			return false;
		}
	}

	private static void collectMeshUvs(Object mesh) throws IllegalAccessException {
		if (!polygonData.getDeclaringClass().isInstance(mesh)) {
			return;
		}
		float[] data = (float[]) polygonData.get(mesh);
		if (data == null) {
			return;
		}
		int amount = polygonAmount.getInt(mesh);
		int max = Math.min(amount, data.length / 23);
		for (int polygon = 0; polygon < max; polygon++) {
			int base = polygon * 23 + 3;
			for (int vertex = 0; vertex < 4; vertex++) {
				int offset = base + vertex * 5;
				THREE_DEE_UVS.add(packUv(data[offset + 3], data[offset + 4]));
			}
		}
	}

	private static boolean isSkinLayersPrimitive(VertexData[] primitive) {
		if (primitive == null || primitive.length == 0) {
			return false;
		}
		for (VertexData vertex : primitive) {
			if (vertex == null || !THREE_DEE_UVS.contains(packUv(vertex.u(), vertex.v()))) {
				return false;
			}
		}
		return true;
	}

	private static long packUv(float u, float v) {
		return ((long) Float.floatToIntBits(u) << 32) | (Float.floatToIntBits(v) & 0xFFFFFFFFL);
	}

	private static void warnOnce(Throwable t) {
		if (!warned) {
			warned = true;
			RealCameraCompatClient.LOGGER.warn("Real Camera Compat: falha na compat com o 3D Skin Layers (faces 3D podem continuar selecionaveis)", t);
		}
	}
}
