package io.github.sefiraat.networks.network.stackcaches;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;

public class ItemStackCache {

    private ItemStack itemStack;
    @Nullable
    private ItemMeta itemMeta = null;

    protected ItemStackCache(@Nullable ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Nullable
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Nullable
    public ItemMeta getItemMeta() {
        if (this.itemMeta == null) {
            this.itemMeta = itemStack.getItemMeta();
        }
        return this.itemMeta;
    }

    @Nullable
    public Material getItemType() {
        return this.itemStack.getType();
    }
}
