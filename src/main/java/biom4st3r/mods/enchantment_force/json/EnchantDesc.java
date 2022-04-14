package biom4st3r.mods.enchantment_force.json;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.Registry;

public record EnchantDesc(Enchantment enchant, int lvl) {
    public JsonEnchantDesc unbuild() {
        return new JsonEnchantDesc(Registry.ENCHANTMENT.getId(this.enchant()).toString(), this.lvl());
    }
}
