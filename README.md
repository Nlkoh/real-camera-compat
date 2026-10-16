# Real Camera Compat

Makes custom player models from EMF/ETF resource packs work correctly with Real Camera.

## The Problem

Real Camera captures the player model by replaying vertex data, bypassing the hook that Entity Model Features (EMF) and Entity Texture Features (ETF) use to activate custom models. Because of this, even with EMF, ETF and a CEM resource pack installed (such as FA Player Extension), Real Camera only captured the vanilla Steve model — with the custom skin applied, but the wrong geometry.

## The Solution

This mod injects the EMF/ETF render state during Real Camera's vertex capture. This activates the correct CEM (Custom Entity Model) for the player, so the custom model is rendered and bound in the capture exactly as other players see it.

## Requirements

- Minecraft 26.2
- Fabric Loader 0.19.5+
- Real Camera 0.7.8-beta+
- Entity Model Features (EMF) 3.3.12+
- Entity Texture Features (ETF) 7.2.6+

## Installation

1. Install Fabric Loader for Minecraft 26.2
2. Download the `.jar` from Modrinth (link coming soon)
3. Place in the `mods/` folder along with Real Camera, EMF and ETF
4. Launch the game

## Compatibility

- Client-side only
- Works with both OpenGL and Vulkan backends
- Tested with FA Player Extension 1.1.0

## License

CC0-1.0
