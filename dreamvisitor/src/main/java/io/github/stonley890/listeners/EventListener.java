package io.github.stonley890.listeners;

import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.shanerx.mojang.Mojang;

import io.github.stonley890.commands.CommandsManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        User user = event.getAuthor();
        String channelId = event.getChannel().getId();
        String username = event.getMessage().getContentStripped();
        String chatChannel = CommandsManager.getChatChannel();
        String whitelistChannel = CommandsManager.getWhitelistChannel();
        String memberRole = CommandsManager.getMemberRole();
        String step3Role = CommandsManager.getStep3Role();

        Pattern p = Pattern.compile("[^a-zA-Z0-9_-_]");

        // If in whitelist channel and username is "legal"
        if (channelId.equals(whitelistChannel) && user.isBot() == false && !p.matcher(username).find()) {
            // Connect to Mojang services
            Mojang mojang = new Mojang();
            mojang.connect();
            // Check for valid UUID
            try {
                mojang.getUUIDOfUsername(username);
                // Get OfflinePlayer from username
                OfflinePlayer player = Bukkit.getOfflinePlayer(username);

                // If player is not whitelisted, add them and change roles
                if (player.isWhitelisted() == false) {
                    Bukkit.getLogger().info("[Dreamvisitor] Whitelisting " + username + ".");
                    player.setWhitelisted(true);
                    // Change roles if assigned
                    if (memberRole != "none") {
                        try {
                            event.getGuild().addRoleToMember(event.getAuthor(),
                                    event.getGuild().getRoleById(memberRole)).queue();
                            event.getGuild().addRoleToMember(event.getAuthor(), event.getGuild().getRoleById(step3Role))
                                    .queue();
                        } catch (HierarchyException exception) {

                        }
                    }
                    // Reply with success
                    event.getMessage().addReaction(Emoji.fromFormatted("✅")).queue();
                } else if (player.isWhitelisted() == true) {
                    // If user is already whitelisted, send error.
                    Bukkit.getLogger().info("[Dreamvisitor] " + username + " is already whitelisted.");
                    event.getMessage().addReaction(Emoji.fromFormatted("❗")).queue();
                    event.getChannel().sendMessage(username + " is already whitelisted!").queue();
                }
            } catch (Exception e) {
                // username does not exist alert
                event.getChannel().sendMessage(username +" does not exist!").queue();
                event.getMessage().addReaction(Emoji.fromFormatted("❌")).queue();
            }
            

        } else if (channelId.equals(whitelistChannel) && user.isBot() == false) {
            // illegal username
            event.getChannel().sendMessage(username + " contains illegal characters!").queue();
            event.getMessage().addReaction(Emoji.fromFormatted("❌")).queue();
        }

        // If in chat channel, send to Minecraft
        if (channelId.equals(chatChannel) && user.isBot() == false) {
            Bukkit.getServer().getOnlinePlayers().forEach(
                    Player -> Player.sendMessage("\u00A73[Discord] \u00A77<" + event.getAuthor().getName() + "> "
                            + event.getMessage().getContentRaw()));

        }
    }
}