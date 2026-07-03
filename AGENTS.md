~~# AGENTS.md — Inventory Dimension

Minecraft 1.21 Forge mod with SpongePowered Mixins.

## Versions
| Component | Version |
|---|---|
| Minecraft | 1.21 |
| Forge | 51.0.33 |
| ForgeGradle | 6.0.24–6.2 |
| Mixin plugin | 0.7.+ |
| Mixin dep | 0.8.5 |
| Java | 21 |
| Mappings | official 1.21 |

All version constants in `gradle.properties`. Never hardcode them.

## Rules
- Package root: `net.whale.inventory_dimension`
- Registry: `DeferredRegister` only; never touch `ForgeRegistries` at class-load time
- `@Mod` class: DeferredRegisters + event bus only
- `reobf = false` — jar ships official Mojang names at runtime
- Never use deprecated NBT methods like `ItemStack.getOrCreateTag()`. Use `ItemStack.get(DataComponentType)` or `ItemStack.set(...)`.
- Prefer @Inject with local capture or @ModifyVariable over @Redirect; use @Overwrite only as an absolute last resort; use official 1.21 names
- Mixin debug exports: `run/.mixin.out/`

## Lessons Learned
### Architecture
### Code & Syntax
### Debugging~~