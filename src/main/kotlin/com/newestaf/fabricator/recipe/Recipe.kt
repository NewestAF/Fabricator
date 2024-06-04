package com.newestaf.fabricator.recipe


import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.newestaf.fabricator.util.ItemStackCoder
import org.bukkit.inventory.ItemStack

class Recipe {
    private val grid: Array<Array<ItemStack?>> = Array(3) { arrayOfNulls(7) }
    private var result: ItemStack? = null

    fun setItem(row: Int, column: Int, item: ItemStack?) {
        if (row !in 0..2 || column !in 0..6) {
            throw IllegalArgumentException("Invalid slot position: [$row, $column]")
        }
        grid[row][column] = item
    }

    fun getItem(row: Int, column: Int): ItemStack? {
        if (row !in 0..2 || column !in 0..6) {
            throw IllegalArgumentException("Invalid slot position: [$row, $column]")
        }
        return grid[row][column]
    }

    fun setResult(item: ItemStack) {
        result = item
    }

    fun getResult(): ItemStack? {
        return result
    }

    fun toJson(): String {
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()

        val jsonObject = JsonObject()

        val gridArray = JsonArray()
        for (row in grid) {
            val rowArray = JsonArray()
            for (item in row) {
                rowArray.add(ItemStackCoder.itemTo64(item))
            }
            gridArray.add(rowArray)
        }
        jsonObject.add("grid", gridArray)

        jsonObject.addProperty("result", ItemStackCoder.itemTo64(result))

        return gson.toJson(jsonObject)
    }


}
