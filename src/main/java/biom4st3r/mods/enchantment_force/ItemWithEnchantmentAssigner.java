package biom4st3r.mods.enchantment_force;

import biom4st3r.mods.enchantment_force.json.EnchantDesc;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;

/**
 * Must not be reimplemented. 
 */
public interface ItemWithEnchantmentAssigner {
    void setEnchantments(Enchantment[] enchants);
    public static void assign(Item i, EnchantDesc[] desc) {
        ((ItemWithEnchantmentAssigner)i).setEnchantments(desc);
    }
    void setEnchantments(EnchantDesc[] desc);
}
