package dev.unnm3d.redischat.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class ItemNameProvider {
    private Method getItemNameMethod;
    private Method hasItemNameMethod;
    private final boolean useItemName;

    public ItemNameProvider(boolean useItemName) {
        try {
            this.getItemNameMethod = ItemMeta.class.getDeclaredMethod("getItemName");
            this.hasItemNameMethod = ItemMeta.class.getDeclaredMethod("hasItemName");
        } catch (NoSuchMethodException ignored) {
            this.useItemName = false;
            Bukkit.getLogger().warning("Failed to find ItemMeta#getItemName() method. Falling back to display name.");
            return;
        }
        this.useItemName = useItemName;
    }

    public String getItemName(ItemStack itemStack) {
        if (getItemNameMethod == null || !useItemName)
            return itemStack.getItemMeta().getDisplayName();
        try {
            return (String) getItemNameMethod.invoke(itemStack.getItemMeta());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the item name as a component, keeping translatable names
     * (e.g. resource pack lang keys) that the legacy String getters flatten into raw keys
     *
     * @param itemStack The item to get the name from
     * @return The name component, or null if unavailable on this platform
     */
    public @Nullable Component getItemNameComponent(ItemStack itemStack) {
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;
        try {
            if (useItemName) {
                return meta.hasItemName() ? meta.itemName() : null;
            }
            return meta.hasDisplayName() ? meta.displayName() : null;
        } catch (NoSuchMethodError e) {
            return null;
        }
    }

    public boolean hasItemName(ItemStack itemMeta) {
        if (hasItemNameMethod == null || !useItemName)
            return itemMeta.getItemMeta().hasDisplayName();
        try {
            return (boolean) hasItemNameMethod.invoke(itemMeta.getItemMeta());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
