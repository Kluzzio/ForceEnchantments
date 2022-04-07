package biom4st3r.mods.enchantment_force.json;

import java.util.stream.Stream;

import biom4st3r.mods.enchantment_force.ItemWithEnchantmentAssigner;

public record ConfigHolder(JsonItemWithEnchantmentConfig[] configs) {
    
    public void execute() {
        Stream.of(configs).map(config -> config.build()).forEach(config -> {
            ItemWithEnchantmentAssigner.assign(config.item(), config.enchants());
        });
    }
}
