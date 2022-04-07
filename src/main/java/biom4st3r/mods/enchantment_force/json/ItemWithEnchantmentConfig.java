package biom4st3r.mods.enchantment_force.json;

import java.util.stream.Stream;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public record ItemWithEnchantmentConfig(
    Item item,
    Enchantment[] enchants
) {
    public JsonItemWithEnchantmentConfig convert() {
        return new JsonItemWithEnchantmentConfig(Registry.ITEM.getId(item).toString(), Stream.of(enchants).map(enchant -> Registry.ENCHANTMENT.getId(enchant).toString()).toArray(String[]::new));
    }
}
