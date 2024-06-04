package com.newestaf.fabricator.service

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.newestaf.fabricator.recipe.Recipe
import com.newestaf.fabricator.util.ItemStackCoder
import org.bukkit.inventory.ItemStack
import java.io.File

object RecipeManager {
    private val recipes = mutableListOf<Recipe>()
    val recipeFile = File(File("plugins", "Fabricator" ), "recipe.json")

    fun addRecipe(recipe: Recipe) {
        recipes.add(recipe)
    }

    fun listRecipes(): List<Recipe> {
        return recipes.toList()
    }

    fun toJson(): String {
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()
        val jsonArray = gson.toJsonTree(recipes.map { it.toJson() }).asJsonArray
        return gson.toJson(jsonArray)
    }

    private fun getRecipeFromJson(json: String): Recipe {
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()

        val recipe = Recipe()

        val jsonObject = gson.fromJson(json, JsonObject::class.java)

        val gridArray = jsonObject.getAsJsonArray("grid")
        for (i in 0 until gridArray.size()) {
            val rowArray = gridArray.get(i).asJsonArray
            for (j in 0 until rowArray.size()) {
                val itemString = rowArray.get(j).asString
                val item: ItemStack? = if (itemString == "EMPTY") {
                    null
                } else {
                    ItemStackCoder.itemFrom64(itemString)
                }
                recipe.setItem(i, j, item)
            }
        }

        val resultString = jsonObject.get("result").asString
        ItemStackCoder.itemFrom64(resultString)?.let { recipe.setResult(it) }

        return recipe
    }

    fun fromJson(json: String) {
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()
        val jsonArray = gson.fromJson(json, Array<String>::class.java)
        recipes.clear()
        for (recipeJson in jsonArray) {
            val recipe = getRecipeFromJson(recipeJson)
            recipes.add(recipe)
        }
    }
}
