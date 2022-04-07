package biom4st3r.mods.enchantment_force;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;

public interface ItemWithEnchantmentAssigner {
    void setEnchantments(Enchantment[] enchants);
    public static void assign(Item i, Enchantment[] enchants) {
        ((ItemWithEnchantmentAssigner)i).setEnchantments(enchants);
    }
}
