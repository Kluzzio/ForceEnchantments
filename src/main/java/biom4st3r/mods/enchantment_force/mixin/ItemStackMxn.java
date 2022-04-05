package biom4st3r.mods.enchantment_force.mixin;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import biom4st3r.mods.enchantment_force.ItemWithEnchantment;
import biom4st3r.mods.enchantment_force.ModInit;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Mixin({ItemStack.class})
public abstract class ItemStackMxn {

    @Shadow
    public abstract NbtCompound getOrCreateNbt();
    @Shadow
    public abstract Item getItem();
    @Shadow
    public abstract void addEnchantment(Enchantment enchantment, int level);

    Object2IntMap<Enchantment> forcedEnchantments = new Object2IntArrayMap<>(0);

    /**
     * if ItemWithEnchantment: deserialize the saved forcedEnchantments
     * @param compound
     * @param ci
     */
    @Inject(method = "<init>(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private void forceEnchantments$init(NbtCompound compound, CallbackInfo ci) {
        if (this.getItem() instanceof ItemWithEnchantment eitem) {
            for (NbtElement ele : (NbtList)compound.get("forced_enchantments")) {
                NbtCompound nbt = (NbtCompound) ele;
                forcedEnchantments.put(Registry.ENCHANTMENT.get(new Identifier(nbt.getString("id"))), nbt.getInt("lvl"));
            }
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
        if (this.getItem() instanceof ItemWithEnchantment eitem) {
            final String KEY = "Enchantments";
            NbtCompound nbt = this.getOrCreateNbt();
            NbtList list = new NbtList();
            for (Enchantment enchant : eitem.getEnchantments()) {
                list.add(EnchantmentHelper.createNbt(EnchantmentHelper.getEnchantmentId(enchant), 1));
                forcedEnchantments.put(enchant, 1);
            }
            nbt.put(KEY, list);
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
        if (this.getItem() instanceof ItemWithEnchantment eitem) {
            final String KEY = "Enchantments";
            if (!this.getOrCreateNbt().contains(KEY)) {
                NbtList list = forcedEnchantments$mapToNbtList();
                this.getOrCreateNbt().put(KEY, list);
            }
            Set<Enchantment> has = ModInit.getEnchantments((NbtList)this.getOrCreateNbt().get(KEY));
            for (Enchantment enchant : eitem.getEnchantments()) {
                if (!has.contains(enchant)) {
                    this.addEnchantment(enchant, forcedEnchantments.getInt(enchant));
                } else if (forcedEnchantments.getInt(enchant) != ModInit.getLevel(enchant, (NbtList) this.getOrCreateNbt().get(KEY))) {
                    this.forcedEnchantments.put(enchant, ModInit.getLevel(enchant, (NbtList) this.getOrCreateNbt().get(KEY)));
                }
            }
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
        if (this.getItem() instanceof ItemWithEnchantment eitem) {
            NbtList list = forcedEnchantments$mapToNbtList();
            nbt.put("forced_enchantments", list);
        }
    }
    
}
