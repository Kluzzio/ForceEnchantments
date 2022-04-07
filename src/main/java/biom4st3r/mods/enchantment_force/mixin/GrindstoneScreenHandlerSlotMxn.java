package biom4st3r.mods.enchantment_force.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import biom4st3r.mods.enchantment_force.ItemWithEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

@Mixin(targets = {"net/minecraft/screen/GrindstoneScreenHandler$4"})
public class GrindstoneScreenHandlerSlotMxn {
    @Inject(method = "getExperience(Lnet/minecraft/item/ItemStack;)I", at = @At("RETURN"), cancellable = true)
    private void forceEnchantments$modOutput(ItemStack stack, CallbackInfoReturnable<Integer> ci) {
        ItemWithEnchantment item = (ItemWithEnchantment) stack.getItem();
        if (item.getEnchantments().length > 0) {
            int output = ci.getReturnValueI();
            for (Enchantment e : item.getEnchantments()) {
                int base_power = e.getMinPower(1);
                output -= Math.max(base_power, e.getMinPower(EnchantmentHelper.getLevel(e, stack)) - 1);
            }
            ci.setReturnValue(output);
        }
    }
}
