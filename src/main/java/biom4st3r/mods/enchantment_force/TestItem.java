package biom4st3r.mods.enchantment_force;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;

public class TestItem extends Item implements ItemWithEnchantment {

    public TestItem() {
        super(new Item.Settings().maxDamage(50));
    }

    @Override
    public Enchantment[] getEnchantments() {
        return new Enchantment[]{Enchantments.FIRE_ASPECT};
    }
    
}
