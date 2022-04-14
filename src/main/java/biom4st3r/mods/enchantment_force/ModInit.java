package biom4st3r.mods.enchantment_force;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import biom4st3r.mods.enchantment_force.json.ConfigHolder;
import biom4st3r.mods.enchantment_force.json.ItemWithEnchantmentConfig;
import biom4st3r.mods.enchantment_force.json.JsonEnchantDesc;
import biom4st3r.mods.enchantment_force.json.JsonItemWithEnchantmentConfig;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModInit implements ModInitializer
{
	public static final String MODID = "enchantment_force";
	public static final Gson GSON = new GsonBuilder()
		.setPrettyPrinting()
		.registerTypeAdapter(JsonEnchantDesc.class, new JsonDeserializer<JsonEnchantDesc>() {
			@Override
			public JsonEnchantDesc deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2)
					throws JsonParseException {
				return new JsonEnchantDesc(arg0.getAsJsonObject().get("enchantment").getAsString(), arg0.getAsJsonObject().get("lvl").getAsInt());
			}
		})
		.registerTypeAdapter(ConfigHolder.class, new JsonDeserializer<ConfigHolder>() {
			@Override
			public ConfigHolder deserialize(JsonElement ele, Type type, JsonDeserializationContext ctx)
					throws JsonParseException {
				
					JsonItemWithEnchantmentConfig[] woop = StreamSupport.stream(ele.getAsJsonObject().get("configs").getAsJsonArray().spliterator(), false)
					.<JsonItemWithEnchantmentConfig>map(member -> ctx.deserialize(member, JsonItemWithEnchantmentConfig.class)).toArray(JsonItemWithEnchantmentConfig[]::new)
					;
				return new ConfigHolder(woop);
			}
		})
		.registerTypeAdapter(JsonItemWithEnchantmentConfig.class, new JsonDeserializer<JsonItemWithEnchantmentConfig>() {
			@Override
			public JsonItemWithEnchantmentConfig deserialize(JsonElement ele, Type arg1,
					JsonDeserializationContext ctx) throws JsonParseException {
				String id = ele.getAsJsonObject().get("itemid").getAsString();
				JsonEnchantDesc[] enchants = StreamSupport.stream(ele.getAsJsonObject().get("enchantments").getAsJsonArray().spliterator(), false)
					.map(member -> {
						if (member.isJsonObject()) {
							return ctx.deserialize(member, JsonEnchantDesc.class);
						} else if(member.isJsonPrimitive()) {
							return new JsonEnchantDesc(member.getAsString(), 1);
						} else {
							throw new JsonParseException("enchantments must be JsonObject with 'enchantment' and 'lvl' key. or String of 'enchantment' name");
						}
					}).toArray(JsonEnchantDesc[]::new);
					;
				return new JsonItemWithEnchantmentConfig(id, enchants);
			}
		})
		.create();

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
	public static void removeEnchantment(Enchantment e, NbtList list) {
		String enchant = Registry.ENCHANTMENT.getId(e).toString();
		IntList toRemove = new IntArrayList();
		for (int i = 0; i < list.size(); i++) {
			NbtCompound compound = list.getCompound(i);
			if (compound.getString(ID_KEY).equals(enchant)) {
				toRemove.add(i - toRemove.size());
			}
		}
		for (int i : toRemove) {
			list.remove(i);
		}
	} 
	// public static List<ItemWithEnchantmentConfig> in_code = Lists.newArrayList();
	public static Map<Object, ItemWithEnchantmentConfig> in_code = Maps.newHashMap();
	public static ConfigHolder CONFIG = null;

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
			CONFIG = ConfigHolder.read();
			// Gather all the enchantments assigned in code
			JsonItemWithEnchantmentConfig[] secondary = in_code.values()
				.stream()
				.map(config -> config.unbuild())
				.toArray(JsonItemWithEnchantmentConfig[]::new);
			// Make a set of all the itemids from the config. This is checked to see if an in_code assignment should be ignored
			Set<String> found = Stream
				.of(CONFIG.configs())
				.map(config -> config.itemid())
				.collect(Collectors.toSet());
			// Combine all configs from file and code, filtering out unneeded code ones.
			CONFIG = new ConfigHolder(Stream.of(
				Stream.of(CONFIG.configs()),
				Stream.of(secondary).filter(config -> !found.contains(config.itemid())))
				.flatMap(s -> s)
				.toArray(JsonItemWithEnchantmentConfig[]::new));
			// save to disk
			ConfigHolder.write(CONFIG);
			CONFIG.execute(); // Must go before clear, because it adds things to clear
			in_code.clear();
		});
		// ItemWithEnchantmentAssigner.assign(Items.STICK, new EnchantDesc[]{new EnchantDesc(Enchantments.FIRE_ASPECT, 2)});
	}


}
