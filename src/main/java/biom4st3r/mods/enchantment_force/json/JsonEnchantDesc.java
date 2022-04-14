package biom4st3r.mods.enchantment_force.json;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public record JsonEnchantDesc(String enchantment, int lvl) {
    public EnchantDesc build() {
        return new EnchantDesc(Registry.ENCHANTMENT.get(new Identifier(this.enchantment())), this.lvl());
    }
}
