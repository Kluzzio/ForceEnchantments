package biom4st3r.mods.enchantment_force.json;

import java.util.stream.Stream;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public record JsonItemWithEnchantmentConfig(
    String itemid,
    JsonEnchantDesc[] enchantemnts
) {
    public ItemWithEnchantmentConfig build() {
        return new ItemWithEnchantmentConfig(
            Registry.ITEM.get(new Identifier(itemid)), 
            Stream
                .of(enchantemnts)
                .map(jdesc -> new EnchantDesc(Registry.ENCHANTMENT.get(new Identifier(jdesc.enchantment())), jdesc.lvl()))
                .toArray(EnchantDesc[]::new)
        );
    }
}
