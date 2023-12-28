package org.hello.foxmcitems;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Foxmc_items extends JavaPlugin implements Listener, CommandExecutor {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("gci").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Give custom tools on /gci command
            ItemStack axe = createCustomTool(Material.NETHERITE_AXE, "Super Axe", 45234);
            ItemStack pickaxe = createCustomTool(Material.NETHERITE_PICKAXE, "Super Pickaxe", 45234);
            ItemStack shovel = createCustomTool(Material.NETHERITE_SHOVEL, "Super Shovel", 45234);

            player.getInventory().addItem(axe, pickaxe, shovel);
            player.sendMessage("You received custom tools!");
            return true;
        }
        return false;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack handItem = player.getInventory().getItemInMainHand();

        // Check if the player is using a custom tool
        if (isCustomTool(handItem.getType())) {
            event.setCancelled(true); // Cancel the normal block breaking

            // Implement your own logic for breaking blocks here
            // You can use the event.getBlock().getType() to get the block type and handle accordingly

            // Example logic for super pickaxe (3x3 area) excluding bedrock
            if (handItem.getType() == Material.NETHERITE_PICKAXE) {
                Block block = event.getBlock();
                if (block.getType() != Material.BEDROCK) {
                    for (int xOffset = -1; xOffset <= 1; xOffset++) {
                        for (int yOffset = -1; yOffset <= 1; yOffset++) {
                            for (int zOffset = -1; zOffset <= 1; zOffset++) {
                                Block targetBlock = block.getRelative(xOffset, yOffset, zOffset);
                                if (targetBlock.getType() != Material.BEDROCK) {
                                    targetBlock.breakNaturally();
                                }
                            }
                        }
                    }
                }
            }

            // Example logic for super shovel (3x3 area) excluding bedrock
            if (handItem.getType() == Material.NETHERITE_SHOVEL) {
                Block block = event.getBlock();
                if (block.getType() != Material.BEDROCK) {
                    for (int xOffset = -1; xOffset <= 1; xOffset++) {
                        for (int zOffset = -1; zOffset <= 1; zOffset++) {
                            Block targetBlock = block.getRelative(xOffset, 0, zOffset);
                            if (targetBlock.getType() != Material.BEDROCK) {
                                targetBlock.breakNaturally();
                            }
                        }
                    }
                }
            }

            // Example logic for super axe (connected logs or wood) excluding bedrock
            if (handItem.getType() == Material.NETHERITE_AXE) {
                Block block = event.getBlock();
                if (isLogOrWood(block.getType())) {
                    breakConnectedLogs(block);
                }
            }
        }
    }

    private void breakConnectedLogs(Block block) {
        Material type = block.getType();
        if (isLogOrWood(type)) {
            block.breakNaturally();
            for (int xOffset = -1; xOffset <= 1; xOffset++) {
                for (int yOffset = -1; yOffset <= 1; yOffset++) {
                    for (int zOffset = -1; zOffset <= 1; zOffset++) {
                        if (!(xOffset == 0 && yOffset == 0 && zOffset == 0)) {
                            Block targetBlock = block.getRelative(xOffset, yOffset, zOffset);
                            if (targetBlock.getType() == type) {
                                breakConnectedLogs(targetBlock);
                            }
                        }
                    }
                }
            }
        }
    }

    private ItemStack createCustomTool(Material material, String displayName, int customModelData) {
        ItemStack item = new ItemStack(material);
        item.setDurability((short) 0); // Make the tool unbreakable

        // Set display name
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setCustomModelData(customModelData);

        // Add enchantments
        itemMeta.addEnchant(Enchantment.MENDING, 1, true);
        itemMeta.addEnchant(Enchantment.DURABILITY, 3, true);
        itemMeta.addEnchant(Enchantment.DIG_SPEED, 5, true);

        item.setItemMeta(itemMeta);

        return item;
    }

    private boolean isCustomTool(Material material) {
        // Add more tools if needed
        return material == Material.NETHERITE_AXE ||
                material == Material.NETHERITE_PICKAXE ||
                material == Material.NETHERITE_SHOVEL;
    }

    private boolean isLogOrWood(Material material) {
        return material == Material.ACACIA_LOG ||
                material == Material.BIRCH_LOG ||
                material == Material.DARK_OAK_LOG ||
                material == Material.JUNGLE_LOG ||
                material == Material.OAK_LOG ||
                material == Material.SPRUCE_LOG ||
                material == Material.ACACIA_WOOD ||
                material == Material.BIRCH_WOOD ||
                material == Material.DARK_OAK_WOOD ||
                material == Material.JUNGLE_WOOD ||
                material == Material.OAK_WOOD ||
                material == Material.SPRUCE_WOOD;
    }
}
