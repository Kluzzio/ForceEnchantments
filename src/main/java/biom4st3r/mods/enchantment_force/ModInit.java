package biom4st3r.mods.enchantment_force;

import java.util.Set;

import com.google.common.collect.Sets;

import net.fabricmc.api.ModInitializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModInit implements ModInitializer
{
	public static final String MODID = "enchantment_force";

    private static final String ID_KEY = "id";
    private static final String LEVEL_KEY = "lvl";

	public static Set<Enchantment> getEnchantments(NbtList list) {
		Set<Enchantment> enchantments = Sets.newHashSet();
		for(NbtElement ele : list) {
			NbtCompound nbt = (NbtCompound) ele;
			enchantments.add(Registry.ENCHANTMENT.get(new Identifier(nbt.getString(ID_KEY))));
		}
		return enchantments;
	}
	public static int getLevel(Enchantment enchantment, NbtList list) {
		for (NbtElement ele : list) {
			NbtCompound nbt = (NbtCompound) ele;
			if (enchantment == Registry.ENCHANTMENT.get(new Identifier(nbt.getString(ID_KEY)))) {
				return nbt.getInt(LEVEL_KEY);
			}
		}
		return -1;
	}

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("test:test"), new TestItem());
	}
}
