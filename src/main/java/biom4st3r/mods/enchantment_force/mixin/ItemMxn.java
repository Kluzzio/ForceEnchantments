package biom4st3r.mods.enchantment_force.mixin;

import org.spongepowered.asm.mixin.Mixin;

import biom4st3r.mods.enchantment_force.ItemWithEnchantment;
import biom4st3r.mods.enchantment_force.ItemWithEnchantmentAssigner;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;

@Mixin({Item.class})
public class ItemMxn implements ItemWithEnchantment, ItemWithEnchantmentAssigner {
    private static final Enchantment[] forcedEnchantment$DEFAULT = new Enchantment[0];
    Enchantment[] forcedEnchantments$enchantments = forcedEnchantment$DEFAULT;
    @Override
    public Enchantment[] getEnchantments() {
        return forcedEnchantments$enchantments;
    }
    @Override
    public void setEnchantments(Enchantment[] enchants) {
        this.forcedEnchantments$enchantments = enchants;
    }
}
