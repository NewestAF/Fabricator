package com.newestaf.fabricator.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.PatternPane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.github.stefvanschie.inventoryframework.pane.util.Pattern
import com.newestaf.fabricator.Fabricator
import com.newestaf.fabricator.recipe.Recipe
import com.newestaf.fabricator.service.RecipeManager
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.io.FileWriter
import java.io.IOException

class GUIManager {

    fun makeFabricatorGUI() : ChestGui {

        val gui = ChestGui(6, "제작기")

        val layoutPattern = Pattern(
            "111111111",
            "100000001",
            "100000001",
            "100000001",
            "111111111",
            "111101111"
        )
        val layoutPane = PatternPane(0, 0, 9, 6, layoutPattern)
        layoutPane.priority = Pane.Priority.LOWEST
        layoutPane.bindItem('1', GuiItem(ItemStack(Material.BLACK_STAINED_GLASS_PANE)) { event ->
            event.isCancelled =
                true
        })
        gui.addPane(layoutPane)

        val controlPane = StaticPane(0, 4, 9, 2)

        val doMakeItem = ItemStack(Material.ANVIL)
        val doMakeItemMeta = doMakeItem.itemMeta
        doMakeItemMeta.displayName(Component.text("제작하기").color(net.kyori.adventure.text.format.TextColor.color(0x00FFFF)))
        doMakeItem.itemMeta = doMakeItemMeta

        val doMakeGuiItem = GuiItem(doMakeItem) { event ->
            event.isCancelled = true

            val inventory = gui.inventory

            for (recipe in RecipeManager.listRecipes()) {
                if (recipe.getResult() == null) {
                    continue
                }

                var validation = 0

                for (row in 0..2) {
                    for (column in 0..6) {
                        val item = recipe.getItem(row, column)
                        if (inventory.getItem((row + 1) * 9 + column + 1) == item ||
                            (item == null && inventory.getItem((row + 1) * 9 + column + 1)?.type == Material.AIR)) {
                            validation++
                        }
                    }
                }

                if (validation == 21) {
                    if (inventory.getItem(49) == recipe.getResult()) {
                        inventory.getItem(49)!!.amount += 1
                    }
                    else {
                        inventory.setItem(49, recipe.getResult())
                    }
                    for (row in 0..2) {
                        for (column in 0..6) {
                            val item = inventory.getItem((row + 1) * 9 + column + 1)
                            if (item != null) {
                                item.amount -= 1
                            }
                        }
                    }
                    event.whoClicked.playSound(Sound.sound(org.bukkit.Sound.BLOCK_ANVIL_USE, Sound.Source.BLOCK, 1.0f, 1.0f))
                    break
                }
                Fabricator.getInstance().logger.info("validation: $validation")
            }

            val player = event.whoClicked
            player.sendMessage("제작하기 버튼을 눌렀습니다.")
        }
        controlPane.addItem(doMakeGuiItem, 4, 0)
        gui.addPane(controlPane)

        return gui

    }

    fun makeRecipeGUI() : ChestGui {
        val gui = ChestGui(6, "레시피")

        val layoutPattern = Pattern(
            "111111111",
            "100000001",
            "100000001",
            "100000001",
            "111111111",
            "111101111"
        )
        val layoutPane = PatternPane(0, 0, 9, 6, layoutPattern)
        layoutPane.priority = Pane.Priority.LOWEST
        layoutPane.bindItem('1', GuiItem(ItemStack(Material.BLACK_STAINED_GLASS_PANE)) { event ->
            event.isCancelled =
                true
        })
        gui.addPane(layoutPane)

        val controlPane = StaticPane(0, 4, 9, 2)

        val makeRecipeItem = ItemStack(Material.ANVIL)
        val makeRecipeItemMeta = makeRecipeItem.itemMeta
        makeRecipeItemMeta.displayName(Component.text("제작하기").color(net.kyori.adventure.text.format.TextColor.color(0x00FFFF)))
        makeRecipeItem.itemMeta = makeRecipeItemMeta

        val makeRecipeGuiItem = GuiItem(makeRecipeItem) { event ->
            event.isCancelled = true
            val player = event.whoClicked

            val inventory = gui.inventory

            val recipe = Recipe()

            for (row in 0..2) {
                for (column in 0 .. 6) {
                    val item = inventory.getItem((row + 1) * 9 + column + 1)

                    if (item != null && item.type != Material.AIR) {
                        recipe.setItem(row, column, item)
                    }
                }
            }

            val result = inventory.getItem(49)
            if (result == null  || result.type == Material.AIR) {
                player.sendMessage("결과 아이템을 설정해주세요.")
            }
            else {
                recipe.setResult(result)
                RecipeManager.addRecipe(recipe)
                var recipeWriter: FileWriter? = null
                try {
                    recipeWriter = FileWriter(RecipeManager.recipeFile)
                    val recipeJson = RecipeManager.toJson()
                    recipeWriter.write(recipeJson)
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    recipeWriter?.close()
                }
                player.sendMessage("레시피를 추가했습니다.")
            }
        }
        controlPane.addItem(makeRecipeGuiItem, 4, 0)
        gui.addPane(controlPane)

        return gui
    }


}
