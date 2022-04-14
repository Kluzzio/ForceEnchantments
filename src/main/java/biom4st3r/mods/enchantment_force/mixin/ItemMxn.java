package biom4st3r.mods.enchantment_force.mixin;

import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;

import biom4st3r.mods.enchantment_force.ItemWithEnchantment;
import biom4st3r.mods.enchantment_force.ItemWithEnchantmentAssigner;
import biom4st3r.mods.enchantment_force.ModInit;
import biom4st3r.mods.enchantment_force.json.EnchantDesc;
import biom4st3r.mods.enchantment_force.json.ItemWithEnchantmentConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;

@Mixin({Item.class})
public class ItemMxn implements ItemWithEnchantment, ItemWithEnchantmentAssigner {
    private static final EnchantDesc[] forcedEnchantment$DEFAULT = new EnchantDesc[0];
    EnchantDesc[] forcedEnchantments$enchantments = forcedEnchantment$DEFAULT;
    @Override
    public EnchantDesc[] getEnchantments() {
        return forcedEnchantments$enchantments;
    }
    @Override
    public void setEnchantments(Enchantment[] enchants) {
        ModInit.in_code.put(this, new ItemWithEnchantmentConfig((Item)(Object)this, enchants));
        this.forcedEnchantments$enchantments = Stream.of(enchants).map(enchant -> new EnchantDesc(enchant, 1)).toArray(EnchantDesc[]::new);
    }
    @Override
    public void setEnchantments(EnchantDesc[] desc) {
        ModInit.in_code.put(this, new ItemWithEnchantmentConfig((Item)(Object)this, desc));
        this.forcedEnchantments$enchantments = desc;
    }
}
