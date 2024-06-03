package com.newestaf.fabricator

import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import com.newestaf.fabricator.command.FabricatorCommand
import com.newestaf.fabricator.recipe.Recipe
import com.newestaf.fabricator.service.RecipeManager
import com.newestaf.fabricator.util.ItemStackCoder
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileWriter
import java.io.IOException


class Fabricator : JavaPlugin() {

    companion object {
        private lateinit var instance: Fabricator

        fun getInstance(): Fabricator {
            return instance
        }
    }

    override fun onEnable() {
        instance = this

        this.getCommand("fabricator")?.setExecutor(FabricatorCommand())

        try {
            val recipeFolder = File("plugins/Fabricator")
            if (!recipeFolder.exists()) {
                recipeFolder.mkdirs()
            }
            val recipeFile = File(recipeFolder, "recipe.json")
            if (recipeFile.createNewFile()) {
                val writer = FileWriter(recipeFile)
                writer.close()
            }
        }
        catch (e: IOException) {
            e.printStackTrace()
        }

        val recipeJson = File("plugins/Fabricator/recipe.json").readText()
        RecipeManager.fromJson(recipeJson)

        logger.info("Fabricator enabled")
    }

    override fun onDisable() {
        logger.info("Fabricator disabled")
    }

}
