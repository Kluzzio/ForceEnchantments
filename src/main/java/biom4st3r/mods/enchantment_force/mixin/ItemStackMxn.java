package biom4st3r.mods.enchantment_force.mixin;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import biom4st3r.mods.enchantment_force.ItemWithEnchantment;
import biom4st3r.mods.enchantment_force.ModInit;
import biom4st3r.mods.enchantment_force.json.EnchantDesc;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Mixin({ItemStack.class})
public abstract class ItemStackMxn {

    @Shadow
    @Final
    private Item item;

    @Shadow
    public abstract NbtCompound getOrCreateNbt();
    // @Shadow
    // public abstract Item getItem();

    Object2IntMap<Enchantment> forcedEnchantments = new Object2IntArrayMap<>(0);

    private boolean forcedEnchantments$shouldIgnore() {
        return this.item == null || ((ItemStack)(Object)this).isOf(Items.AIR);
    }

    /**
     * if ItemWithEnchantment: deserialize the saved forcedEnchantments
     * @param compound
     * @param ci
     */
    @Inject(method = "<init>(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private void forceEnchantments$init(NbtCompound compound, CallbackInfo ci) {
        if (this.forcedEnchantments$shouldIgnore()) return;
        final ItemWithEnchantment eitem = (ItemWithEnchantment) this.item;
        // If has forced enchatments assigned
        if (eitem.getEnchantments().length > 0) {
            // ModInit.mixin();
            // Deserialize forcedEnchantments
            for (NbtElement ele : (NbtList)compound.get("forced_enchantments")) {
                NbtCompound nbt = (NbtCompound) ele;
                forcedEnchantments.put(Registry.ENCHANTMENT.get(new Identifier(nbt.getString("id"))), nbt.getInt("lvl"));
            }

            ModInit.visitEnchantments(this.getOrCreateNbt(), enchant -> {
                for (EnchantDesc desc : eitem.getEnchantments()) {
                    if (this.forcedEnchantments.getInt(desc.enchant()) < desc.lvl()) {
                        this.forcedEnchantments.put(desc.enchant(), desc.lvl());
                    }
                    if (enchant.getInt(desc.enchant()) < desc.lvl()) {
                        enchant.put(desc.enchant(), desc.lvl());
                    }
                }

                // Remove forcedEnchantments that were serialized, but are no longer in the config
                Set<Enchantment> enchantmentsOnItem = Stream.of(eitem.getEnchantments()).map(desc -> desc.enchant()).collect(Collectors.toSet());
                Enchantment[] toRemove = forcedEnchantments.keySet().stream().filter(e -> !enchantmentsOnItem.contains(e)).toArray(Enchantment[]::new);
                for (Enchantment e : toRemove) {
                    forcedEnchantments.removeInt(e);
                    enchant.removeInt(e);
                }
            });
        }
    }

    /**
     * Initlize
     * @param item
     * @param count
     * @param ci
     */
    @Inject(method = "<init>(Lnet/minecraft/item/ItemConvertible;I)V", at = @At("TAIL"))
    private void forceEnchantments$init2(ItemConvertible item, int count, CallbackInfo ci) {
        if (this.forcedEnchantments$shouldIgnore()) return;
        final ItemWithEnchantment eitem = (ItemWithEnchantment) this.item;

        // If has forced enchatments assigned
        if (eitem.getEnchantments().length > 0) {
            // ModInit.mixin();
            ModInit.visitEnchantments(this.getOrCreateNbt(), enchants -> {
                for (EnchantDesc desc : eitem.getEnchantments()) {
                    enchants.put(desc.enchant(), desc.lvl());
                    forcedEnchantments.put(desc.enchant(), desc.lvl());
                }
            });
        }
    }

    /**
     * if ItemWithEnchantment:
     * Go through the all the enchantments: if this stack doesn't have a required one add it from the map
     * if this stacks level doesn't match the map: update the map
     * @param ci
     */
    @Inject(method = "getEnchantments", at = @At("RETURN"), cancellable = true)
    private void forceEnchantments$getEnchantments(CallbackInfoReturnable<NbtList> ci) {
        if (this.forcedEnchantments$shouldIgnore()) return;
        final ItemWithEnchantment eitem = (ItemWithEnchantment) this.item;
        // If has forced enchatments assigned
        if (eitem.getEnchantments().length > 0) {
            ModInit.visitEnchantments(this.getOrCreateNbt(), enchants -> {
                for (EnchantDesc desc : eitem.getEnchantments()) {
                    if (!enchants.containsKey(desc.enchant())) {
                        enchants.put(desc.enchant(), desc.lvl());
                    } else if (forcedEnchantments.getInt(desc.enchant()) != enchants.getInt(desc.enchant())) {
                        this.forcedEnchantments.put(desc.enchant(), enchants.getInt(desc.enchant()));
                    }
                }
            });
        }
    }

    private NbtList forcedEnchantments$mapToNbtList() {
        NbtList list = new NbtList();
        for (Entry<Enchantment> i : forcedEnchantments.object2IntEntrySet()) {
            NbtCompound compound = new NbtCompound();
            compound.putString("id", EnchantmentHelper.getEnchantmentId(i.getKey()).toString());
            compound.putInt("lvl", i.getIntValue());
            list.add(compound);
        }
        return list;
    }

    /**
     * save forced enchantments
     * @param nbt
     * @param ci
     */
    @Inject(method = "writeNbt", at = @At("TAIL"))
    private void forceEnchantments$writeNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> ci) {
        if (this.forcedEnchantments$shouldIgnore()) return;
        ItemWithEnchantment eitem = (ItemWithEnchantment) this.item;
        if (eitem.getEnchantments().length > 0) {

            // If has forced enchatments assigned: serialize forcedEnchantments map
            NbtList list = forcedEnchantments$mapToNbtList();
            nbt.put("forced_enchantments", list);
        }
    }
    
}
