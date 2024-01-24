package fr.shoqapik.brokenitems;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.ArrowDamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BrokenItemsEvents {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBreakingBlock(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        handleEvent(player, player.getMainHandItem(), event);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        handleEvent(player, player.getMainHandItem(), event);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        handleEvent(player, event.getItemStack(), event);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        handleEvent(player, event.getItemStack(), event);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        handleEvent(player, event.getItemStack(), event);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack tool = event.getItemStack();
        if(!tool.isEmpty() && tool.isDamageableItem() && isItemBroken(tool)){
            event.getToolTip().add(Component.literal(ChatFormatting.RED+"Broken item"));
        }

    }

    @SubscribeEvent
    public void onAttributeModifierApplied(ItemAttributeModifierEvent event){
        ItemStack stack = event.getItemStack();
        if(isItemBroken(stack) && isArmor(stack)){
            event.clearModifiers();
        }
    }

    @SubscribeEvent
    public void onAnvilRepair(AnvilRepairEvent event) {
        ItemStack left = event.getLeft();
        if (isItemBroken(left) && left.getTag() != null) {
            if (!left.getTag().contains("Enchantments", 9)) {
                left.getTag().put("Enchantments", new ListTag());
            }

            event.getRight().getEnchantmentTags().add(left.getTag().getList("Enchantments", 10));
        }
    }

    private boolean isArmor(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem;
    }

    public void handleEvent(Player player, ItemStack tool, Event event){
        if(!tool.isEmpty() && tool.isDamageableItem() && isItemBroken(tool)){
            event.setCanceled(true);
        }
    }

    public static boolean isItemBroken(ItemStack stack){
        return stack.getMaxDamage() > 1 && stack.getDamageValue() >= stack.getMaxDamage() - 1;
    }
}
