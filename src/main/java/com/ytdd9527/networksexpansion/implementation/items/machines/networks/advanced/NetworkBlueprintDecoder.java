package com.ytdd9527.networksexpansion.implementation.items.machines.networks.advanced;

import com.ytdd9527.networksexpansion.core.items.unusable.AbstractBlueprint;
import com.ytdd9527.networksexpansion.implementation.items.ExpansionItems;
import com.ytdd9527.networksexpansion.utils.itemstacks.BlockMenuUtil;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.network.stackcaches.BlueprintInstance;
import io.github.sefiraat.networks.slimefun.network.NetworkObject;
import io.github.sefiraat.networks.utils.Keys;
import io.github.sefiraat.networks.utils.datatypes.DataTypeMethods;
import io.github.sefiraat.networks.utils.datatypes.PersistentCraftingBlueprintType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;

public class NetworkBlueprintDecoder extends NetworkObject {
    private static final int[] BACKGROUND_SLOTS = {0, 1, 2, 3, 4, 5, 9, 11, 12, 14, 18, 19, 20, 21, 22, 23};
    private static final int[] OUTPUT_SLOTS = {6, 7, 8, 15, 16, 17, 24, 25, 26};
    private static final int INPUT_SLOT = 10;
    private static final int DECODE_SLOT = 13;
    private static final CustomItemStack DECODE_ITEM = new CustomItemStack(
            Material.KNOWLEDGE_BOOK,
            "&6网络解码器",
            "&7点击解码网络蓝图"
    );
    public NetworkBlueprintDecoder(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.DECODER);
    }

    @Override
    public void postRegister() {
        new BlockMenuPreset(this.getId(), this.getItemName()) {

            @Override
            public void init() {
                setSize(27);
                for (int slot : BACKGROUND_SLOTS) {
                    addItem(slot, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
                }
                addItem(DECODE_SLOT, DECODE_ITEM);
            }

            @Override
            public void newInstance(@Nonnull BlockMenu menu, @Nonnull Block b) {
                addMenuClickHandler(DECODE_SLOT, (player, slot, clickedItem, clickAction) -> {
                    decode(menu);
                    return false;
                });
            }

            @Override
            public boolean canOpen(@Nonnull Block block, @Nonnull Player player) {
                return player.hasPermission("slimefun.inventory.bypass") || (ExpansionItems.ADVANCED_EXPORT.canUse(player, false)
                        && Slimefun.getProtectionManager().hasPermission(player, block.getLocation(), Interaction.INTERACT_BLOCK));
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow flow) {
                if (flow == ItemTransportFlow.INSERT) {
                    return new int[]{getInputSlot()};
                }

                return getOutputSlots();
            }
        };
    }

    public static int[] getBackgroundSlots() {
        return BACKGROUND_SLOTS;
    }

    public static int[] getOutputSlots() {
        return OUTPUT_SLOTS;
    }

    public static int getInputSlot() {
        return INPUT_SLOT;
    }

    public static int getDecodeSlot() {
        return DECODE_SLOT;
    }

    private void decode(BlockMenu menu) {
        ItemStack input = menu.getItemInSlot(getInputSlot());
        if (input == null || input.getType().isAir()) {
            return;
        }

        SlimefunItem item = SlimefunItem.getByItem(input);
        if (!(item instanceof AbstractBlueprint)) {
            return;
        }

        ItemMeta meta = input.getItemMeta();
        if (meta == null) {
            return;
        }

        BlueprintInstance blueprintInstance = DataTypeMethods.getCustom(meta, Keys.BLUEPRINT_INSTANCE, PersistentCraftingBlueprintType.TYPE);
        if (blueprintInstance == null) {
            blueprintInstance = DataTypeMethods.getCustom(meta, Keys.BLUEPRINT_INSTANCE2, PersistentCraftingBlueprintType.TYPE);
        }
        if (blueprintInstance == null) {
            blueprintInstance = DataTypeMethods.getCustom(meta, Keys.BLUEPRINT_INSTANCE3, PersistentCraftingBlueprintType.TYPE);
        }
        if (blueprintInstance == null) {
            return;
        }

        ItemStack[] inputs = blueprintInstance.getRecipeItems();
        for (ItemStack inputItem : inputs) {
            ItemStack left = BlockMenuUtil.pushItem(menu, inputItem, getOutputSlots());
            if (left != null) {
                menu.getLocation().getWorld().dropItem(menu.getLocation(), left);
            }
        }
    }
}
