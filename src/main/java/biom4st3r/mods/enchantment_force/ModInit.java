package biom4st3r.mods.enchantment_force;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.stream.StreamSupport;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import biom4st3r.mods.enchantment_force.json.ConfigHolder;
import biom4st3r.mods.enchantment_force.json.JsonItemWithEnchantmentConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
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
					JsonDeserializationContext arg2) throws JsonParseException {
				String id = ele.getAsJsonObject().get("itemid").getAsString();
				String[] enchants = StreamSupport.stream(ele.getAsJsonObject().get("enchantments").getAsJsonArray().spliterator(), false)
					.<String>map(member -> member.getAsString()).toArray(String[]::new);
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

	@Override
	public void onInitialize() {
		File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "forceEnchantments.json");
		// file.mkdirs();
		if (!file.exists()) {
			try (FileOutputStream stream = new FileOutputStream(file)) {
				stream.write(GSON.toJson(new ConfigHolder(new JsonItemWithEnchantmentConfig[0])).getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try (FileInputStream stream = new FileInputStream(file)) {
				ConfigHolder holder = GSON.fromJson(new String(stream.readAllBytes()), ConfigHolder.class);
				holder.execute();
			} catch (JsonSyntaxException | IOException e) {
				e.printStackTrace();
			}
		}
		Registry.register(Registry.ITEM, new Identifier("test:test"), new TestItem());
	}
}
