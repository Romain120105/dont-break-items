package fr.shoqapik.brokenitems.mixins;

import fr.shoqapik.brokenitems.BrokenItemsEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();


    @Shadow @Nullable public abstract CompoundTag getTag();

    @Shadow public abstract void setTag(@org.jetbrains.annotations.Nullable CompoundTag p_41752_);

    /**
     * @author Shoqapik
     * @reason Prevent items from being destroyed
     */
    @Overwrite
    public <T extends LivingEntity> void hurtAndBreak(int p_41623_, T p_41624_, Consumer<T> p_41625_) {
        ItemStack stack = (ItemStack)(Object) this;

        if (!p_41624_.level.isClientSide && (!(p_41624_ instanceof Player) || !((Player)p_41624_).getAbilities().instabuild)) {
            if (stack.isDamageableItem()) {
                p_41623_ = this.getItem().damageItem(stack, p_41623_, p_41624_, p_41625_);
                if (stack.hurt(p_41623_, p_41624_.getRandom(), p_41624_ instanceof ServerPlayer ? (ServerPlayer)p_41624_ : null)) {
                    if(stack.isDamageableItem() && BrokenItemsEvents.isItemBroken(stack)){
                        stack.setDamageValue(stack.getMaxDamage() -1);
                    }else {
                        p_41625_.accept(p_41624_);
                        Item item = this.getItem();
                        stack.shrink(1);
                        if (p_41624_ instanceof Player) {
                            ((Player) p_41624_).awardStat(Stats.ITEM_BROKEN.get(item));
                        }

                        stack.setDamageValue(0);
                    }
                }

            }
        }
    }

    /**
     * @author Shoqapik
     * @reason Temp remove enchantments when item is broken
     */
    @Overwrite
    public ListTag getEnchantmentTags() {
        ItemStack stack = (ItemStack)(Object) this;
        if(stack.isDamageableItem() && BrokenItemsEvents.isItemBroken(stack)) {
            return new ListTag();
        }

        return this.getTag() != null ? this.getTag().getList("Enchantments", 10) : new ListTag();
    }


}
