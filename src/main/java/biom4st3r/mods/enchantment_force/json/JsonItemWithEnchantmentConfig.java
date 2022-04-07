package biom4st3r.mods.enchantment_force.json;

import java.util.stream.Stream;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public record JsonItemWithEnchantmentConfig(
    String itemid,
    String[] enchantemnts
) {
    public ItemWithEnchantmentConfig build() {
        return new ItemWithEnchantmentConfig(
            Registry.ITEM.get(new Identifier(itemid)), 
            Stream.of(enchantemnts).map(s -> Registry.ENCHANTMENT.get(new Identifier(s))).toArray(Enchantment[]::new)
        );
    }
}
