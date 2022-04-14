package biom4st3r.mods.enchantment_force;

import org.jetbrains.annotations.ApiStatus.Internal;

import biom4st3r.mods.enchantment_force.json.EnchantDesc;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;

/**
 * Must not be reimplemented. 
 */
public interface ItemWithEnchantmentAssigner {
    void setEnchantments(Enchantment[] enchants);
    @Internal
    public static void assign(Item i, Enchantment[] enchants) {
        ((ItemWithEnchantmentAssigner)i).setEnchantments(enchants);
    }
    public static void assign(Item i, EnchantDesc[] desc) {
        ((ItemWithEnchantmentAssigner)i).setEnchantments(desc);
    }
    void setEnchantments(EnchantDesc[] desc);
}
