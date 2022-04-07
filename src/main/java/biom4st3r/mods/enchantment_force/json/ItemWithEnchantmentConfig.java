package biom4st3r.mods.enchantment_force.json;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;

public record ItemWithEnchantmentConfig(
    Item item,
    Enchantment[] enchants
) {
    
}
