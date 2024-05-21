package io.github.stonley890.dreamvisitor.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import io.github.stonley890.dreamvisitor.Bot;
import io.github.stonley890.dreamvisitor.Dreamvisitor;
import org.jetbrains.annotations.NotNull;

public class CmdPausechat implements DVCommand {

    final Dreamvisitor plugin = Dreamvisitor.getPlugin();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // pausechat

        // If chat is paused, unpause. If not, pause
        if (Dreamvisitor.chatPaused) {

            // Change settings
            Dreamvisitor.chatPaused = false;
            plugin.getConfig().set("chatPaused", Dreamvisitor.chatPaused);

            // Broadcast to server
            Bukkit.getServer().broadcastMessage(ChatColor.BLUE + "Chat has been unpaused.");
            
            // Broadcast to chat channel
            Bot.getGameChatChannel().sendMessage("**Chat has been unpaused. Messages will now be sent to Minecraft**").queue();

        } else {

            // Change settings
            Dreamvisitor.chatPaused = true;
            plugin.getConfig().set("chatPaused", Dreamvisitor.chatPaused);

            // Broadcast to server
            Bukkit.getServer().broadcastMessage(ChatColor.BLUE + "Chat has been paused.");

            // Broadcast to chat channel
            Bot.getGameChatChannel().sendMessage("**Chat has been paused. Messages will not be sent to Minecraft**").queue();

        }
        plugin.saveConfig();
        return true;
    }

    @NotNull
    @Override
    public String getCommandName() {
        return "pausechat";
    }

    @Override
    public LiteralCommandNode<?> getNode() {
        return LiteralArgumentBuilder.literal(getCommandName()).build();
    }
}
