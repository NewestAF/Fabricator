package com.newestaf.fabricator.command

import com.newestaf.fabricator.gui.GUIManager
import com.newestaf.fabricator.service.RecipeManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.FileWriter
import java.io.IOException

class FabricatorCommand : CommandExecutor {

    private val guiManager = GUIManager()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("This command can only be executed by a player.")
            return true
        }

        if (args.isNotEmpty()) {
            if (args[0].equals("open", ignoreCase = true)) {
                val gui = guiManager.makeFabricatorGUI()
                gui.show(sender)
                return true
            }
            if (args[0].equals("set", ignoreCase = true)) {
                val gui = guiManager.makeRecipeGUI()
                gui.show(sender)
                return true
            }
        }

        sender.sendMessage("Usage: /fabricator open")
        return false
    }


}
