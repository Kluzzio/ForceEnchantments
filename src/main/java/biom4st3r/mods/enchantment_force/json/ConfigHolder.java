package biom4st3r.mods.enchantment_force.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.stream.Stream;

import com.google.gson.JsonSyntaxException;

import biom4st3r.mods.enchantment_force.ItemWithEnchantmentAssigner;
import biom4st3r.mods.enchantment_force.ModInit;
import net.fabricmc.loader.api.FabricLoader;

public record ConfigHolder(JsonItemWithEnchantmentConfig[] configs) {
    
    public void execute() {
        Stream.of(configs).map(config -> config.build()).forEach(config -> {
            ItemWithEnchantmentAssigner.assign(config.item(), config.enchants());
        });
    }
    private static File getFile() {
        return  new File(FabricLoader.getInstance().getConfigDir().toFile(), "forceEnchantments.json");
    }

    public static ConfigHolder read() {
		File file = getFile();
		// file.mkdirs();
		if (!file.exists()) {
			try (FileOutputStream stream = new FileOutputStream(file)) {
				stream.write(ModInit.GSON.toJson(new ConfigHolder(new JsonItemWithEnchantmentConfig[0])).getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try (FileInputStream stream = new FileInputStream(file)) {
				ConfigHolder holder = ModInit.GSON.fromJson(new String(stream.readAllBytes()), ConfigHolder.class);
				return holder;
			} catch (JsonSyntaxException | IOException e) {
				e.printStackTrace();
			}
		}
        return new ConfigHolder(new JsonItemWithEnchantmentConfig[0]);
    }

    public static void write(ConfigHolder holder) {
        File file = getFile();
        try (FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(ModInit.GSON.toJson(holder).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
