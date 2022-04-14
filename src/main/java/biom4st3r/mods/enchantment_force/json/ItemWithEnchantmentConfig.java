package biom4st3r.mods.enchantment_force.json;

import java.util.stream.Stream;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public record ItemWithEnchantmentConfig(
    Item item,
    EnchantDesc[] enchants
) {
    public ItemWithEnchantmentConfig(Item item, Enchantment[] enchants) {
        this(item, Stream.of(enchants).map(enchant->new EnchantDesc(enchant,1)).toArray(EnchantDesc[]::new));
    }

    public JsonItemWithEnchantmentConfig unbuild() {
        return new JsonItemWithEnchantmentConfig(
            Registry.ITEM.getId(item).toString(), 
            Stream
                .of(enchants)
                .map(desc -> new JsonEnchantDesc(
                    Registry.ENCHANTMENT.getId(desc.enchant()).toString(), 
                    desc.lvl())
                )
                .toArray(JsonEnchantDesc[]::new));
    }
}
