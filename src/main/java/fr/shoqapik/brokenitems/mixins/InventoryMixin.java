package fr.shoqapik.brokenitems.mixins;

import fr.shoqapik.brokenitems.BrokenItemsEvents;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Inventory.class)
public abstract class InventoryMixin {

    @Shadow @Final private List<NonNullList<ItemStack>> compartments;

    @Shadow @Final public Player player;

    @Shadow public int selected;

    @Shadow @Final public NonNullList<ItemStack> armor;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void tick() {
        for(NonNullList<ItemStack> nonnulllist : this.compartments) {
            for(int i = 0; i < nonnulllist.size(); ++i) {
                if (!nonnulllist.get(i).isEmpty()) {
                    nonnulllist.get(i).inventoryTick(this.player.level, this.player, i, this.selected == i);
                }
            }
        }
        armor.forEach(e -> onArmorTick(e));
    }

    public void onArmorTick(ItemStack e){
        if(e.isDamageableItem() && BrokenItemsEvents.isItemBroken(e)){

        }else {
            e.onArmorTick(player.level, player);
        }
    }

}
