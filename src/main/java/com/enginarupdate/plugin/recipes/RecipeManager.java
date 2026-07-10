package com.enginarupdate.plugin.recipes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.plugin.Plugin;

import java.util.Iterator;
import java.util.Map;

/**
 * Adds and removes custom crafting recipes. The Netherite Upgrade Smithing Template
 * flow was later restored: vanilla's own recipes for it are removed and replaced with
 * a single custom crafting recipe (its sole source) plus custom Smithing recipes that
 * use Blaze Rod as the addition material instead of Netherite Ingot.
 */
public final class RecipeManager {

    private static final Map<Material, Material> DIAMOND_TO_NETHERITE = Map.ofEntries(
            Map.entry(Material.DIAMOND_HELMET, Material.NETHERITE_HELMET),
            Map.entry(Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE),
            Map.entry(Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS),
            Map.entry(Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS),
            Map.entry(Material.DIAMOND_SWORD, Material.NETHERITE_SWORD),
            Map.entry(Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE),
            Map.entry(Material.DIAMOND_AXE, Material.NETHERITE_AXE),
            Map.entry(Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL),
            Map.entry(Material.DIAMOND_HOE, Material.NETHERITE_HOE),
            Map.entry(Material.DIAMOND_SPEAR, Material.NETHERITE_SPEAR)
    );

    private RecipeManager() {
    }

    public static void apply(Plugin plugin) {
        removeNetheriteUpgradeTemplateRecipes();
        removeVanillaRecipesToReplace();
        registerNewRecipes(plugin);
        registerNetheriteUpgradeRecipes(plugin);
    }

    /**
     * Clears vanilla's own recipes for the Netherite Upgrade Smithing Template (both the
     * upgrade and the duplication recipe), so only our custom recipes remain in effect
     * (see registerNetheriteUpgradeRecipes).
     */
    private static void removeNetheriteUpgradeTemplateRecipes() {
        Iterator<Recipe> it = Bukkit.recipeIterator();
        while (it.hasNext()) {
            Recipe recipe = it.next();
            if (recipe instanceof SmithingTransformRecipe smithing
                    && choiceMatches(smithing.getTemplate(), Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE)) {
                it.remove();
                continue;
            }
            if ((recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe)
                    && recipe.getResult().getType() == Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE) {
                it.remove();
            }
        }
    }

    private static boolean choiceMatches(RecipeChoice choice, Material material) {
        if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
            return materialChoice.getChoices().contains(material);
        }
        if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
            return exactChoice.getChoices().stream().anyMatch(item -> item.getType() == material);
        }
        return false;
    }

    /**
     * Removes the vanilla recipe before the new one that replaces it is registered
     * (Netherite Ingot, Ender Eye, Book, Beacon).
     */
    private static void removeVanillaRecipesToReplace() {
        Bukkit.removeRecipe(NamespacedKey.minecraft("ender_eye"));
        Bukkit.removeRecipe(NamespacedKey.minecraft("book"));
        Bukkit.removeRecipe(NamespacedKey.minecraft("beacon"));
    }

    private static void registerNewRecipes(Plugin plugin) {
        // 2. Netherite armor
        addShaped(plugin, "netherite_helmet",
                new ItemStack(Material.NETHERITE_HELMET),
                new String[]{"XXX", "X_X"},
                'X', Material.NETHERITE_INGOT);
        addShaped(plugin, "netherite_chestplate",
                new ItemStack(Material.NETHERITE_CHESTPLATE),
                new String[]{"X_X", "XXX", "XXX"},
                'X', Material.NETHERITE_INGOT);
        addShaped(plugin, "netherite_leggings",
                new ItemStack(Material.NETHERITE_LEGGINGS),
                new String[]{"XXX", "X_X", "X_X"},
                'X', Material.NETHERITE_INGOT);
        addShaped(plugin, "netherite_boots",
                new ItemStack(Material.NETHERITE_BOOTS),
                new String[]{"X_X", "X_X"},
                'X', Material.NETHERITE_INGOT);

        // 3. Netherite tool set (Ingot + Blaze Rod)
        addShaped(plugin, "netherite_shovel",
                new ItemStack(Material.NETHERITE_SHOVEL),
                new String[]{"_N_", "_R_", "_R_"},
                'N', Material.NETHERITE_INGOT,
                'R', Material.BLAZE_ROD);
        addShaped(plugin, "netherite_hoe",
                new ItemStack(Material.NETHERITE_HOE),
                new String[]{"NN_", "_R_", "_R_"},
                'N', Material.NETHERITE_INGOT,
                'R', Material.BLAZE_ROD);
        addShaped(plugin, "netherite_sword",
                new ItemStack(Material.NETHERITE_SWORD),
                new String[]{"_N_", "_N_", "_R_"},
                'N', Material.NETHERITE_INGOT,
                'R', Material.BLAZE_ROD);
        addShaped(plugin, "netherite_axe",
                new ItemStack(Material.NETHERITE_AXE),
                new String[]{"NN_", "NR_", "_R_"},
                'N', Material.NETHERITE_INGOT,
                'R', Material.BLAZE_ROD);
        addShaped(plugin, "netherite_pickaxe",
                new ItemStack(Material.NETHERITE_PICKAXE),
                new String[]{"NNN", "_R_", "_R_"},
                'N', Material.NETHERITE_INGOT,
                'R', Material.BLAZE_ROD);

        // 4. Netherite Spear
        addShaped(plugin, "netherite_spear",
                new ItemStack(Material.NETHERITE_SPEAR),
                new String[]{"N__", "_R_", "__R"},
                'N', Material.NETHERITE_INGOT,
                'R', Material.BLAZE_ROD);

        // 5. Ender Eye
        addShaped(plugin, "ender_eye",
                new ItemStack(Material.ENDER_EYE),
                new String[]{"#_G", "#oG", "##E"},
                'G', Material.GHAST_TEAR,
                '#', Material.BLAZE_POWDER,
                'o', Material.ENDER_PEARL,
                'E', Material.ECHO_SHARD);

        // 7. Wind Charge
        addShaped(plugin, "wind_charge",
                new ItemStack(Material.WIND_CHARGE, 8),
                new String[]{"___", "_#o", "__#"},
                '#', Material.PHANTOM_MEMBRANE,
                'o', Material.ENDER_PEARL);

        // 9. Beacon (replaces the vanilla recipe)
        addShaped(plugin, "beacon",
                new ItemStack(Material.BEACON),
                new String[]{"###", "NoN", "OOO"},
                '#', Material.GLASS,
                'N', Material.NETHER_STAR,
                'o', Material.DIAMOND_BLOCK,
                'O', Material.OBSIDIAN);

        // 10. Book (replaces the vanilla recipe)
        addShaped(plugin, "book",
                new ItemStack(Material.BOOK, 3),
                new String[]{"_#_", "###", "L##"},
                '#', Material.PAPER,
                'L', Material.LEATHER);

        // 12. Iron Golem Spawn Egg
        addShaped(plugin, "iron_golem_spawn_egg",
                new ItemStack(Material.IRON_GOLEM_SPAWN_EGG),
                new String[]{"_C_", "###", "_#_"},
                'C', Material.CARVED_PUMPKIN,
                '#', Material.IRON_BLOCK);

        // 13. Wither Spawn Egg
        addShaped(plugin, "wither_spawn_egg",
                new ItemStack(Material.WITHER_SPAWN_EGG),
                new String[]{"WWW", "###", "_#_"},
                'W', Material.WITHER_SKELETON_SKULL,
                '#', Material.SOUL_SAND);

        // 14. Snow Golem Spawn Egg
        addShaped(plugin, "snow_golem_spawn_egg",
                new ItemStack(Material.SNOW_GOLEM_SPAWN_EGG),
                new String[]{"_C_", "_#_", "_#_"},
                'C', Material.CARVED_PUMPKIN,
                '#', Material.SNOW_BLOCK);

        // 15. Mending-enchanted Elytra (sole exception to the Enchanting Table/Anvil ban)
        ItemStack mendingElytra = new ItemStack(Material.ELYTRA);
        mendingElytra.addUnsafeEnchantment(Enchantment.MENDING, 1);
        addShaped(plugin, "mending_elytra",
                mendingElytra,
                new String[]{"#M#", "CEB", "PHP"},
                '#', Material.TORCHFLOWER,
                'M', Material.MUSIC_DISC_5,
                'C', Material.CREAKING_HEART,
                'E', Material.ELYTRA,
                'B', Material.BEACON,
                'P', Material.PITCHER_PLANT,
                'H', Material.HEAVY_CORE);
    }

    /**
     * Netherite upgrade path restored: this crafting recipe is the template's sole
     * source (since it was removed from loot); the upgrade uses Blaze Rod as the
     * addition material instead of Netherite Ingot.
     */
    private static void registerNetheriteUpgradeRecipes(Plugin plugin) {
        addShaped(plugin, "netherite_upgrade_smithing_template",
                new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                new String[]{"o#o", "###", "o#o"},
                'o', Material.DIAMOND,
                '#', Material.NETHERITE_INGOT);

        RecipeChoice template = new RecipeChoice.MaterialChoice(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
        RecipeChoice addition = new RecipeChoice.MaterialChoice(Material.BLAZE_ROD);

        for (Map.Entry<Material, Material> entry : DIAMOND_TO_NETHERITE.entrySet()) {
            Material diamondType = entry.getKey();
            Material netheriteType = entry.getValue();
            NamespacedKey key = new NamespacedKey(plugin, "netherite_upgrade_" + diamondType.name().toLowerCase());
            RecipeChoice base = new RecipeChoice.MaterialChoice(diamondType);
            Bukkit.addRecipe(new SmithingTransformRecipe(key, new ItemStack(netheriteType), template, base, addition));
        }
    }

    private static void addShaped(Plugin plugin, String id, ItemStack result, String[] shape, Object... ingredients) {
        NamespacedKey key = new NamespacedKey(plugin, id);
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape(normalizeShape(shape));
        for (int i = 0; i < ingredients.length; i += 2) {
            char symbol = (Character) ingredients[i];
            Material material = (Material) ingredients[i + 1];
            recipe.setIngredient(symbol, material);
        }
        Bukkit.addRecipe(recipe);
    }

    /**
     * ShapedRecipe expects ' ' for empty cells; we use '_' for readability instead.
     */
    private static String[] normalizeShape(String[] shape) {
        String[] result = new String[shape.length];
        for (int i = 0; i < shape.length; i++) {
            result[i] = shape[i].replace('_', ' ');
        }
        return result;
    }
}
