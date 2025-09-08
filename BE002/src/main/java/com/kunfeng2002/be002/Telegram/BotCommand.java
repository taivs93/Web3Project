package com.kunfeng2002.be002.Telegram;

import java.util.Arrays;
import java.util.Optional;

public enum BotCommand {
    START("/start", "Start the bot"),
    HELP("/help", "Show all available commands"),
    MENU("/menu", "Show menu"),
    SETTINGS("/settings", "Adjust your preferences"),
    HISTORY("/history", "View transaction history"),
                    
    FOLLOW("/follow", "Followed wallet: "),
    UNFOLLOW("/unfollow", "Unfollowed wallet: "),

    PRICE("/price", "Get current token prices"),
    BALANCE("/balance", "Check your balance"),
    LINK("/link_", "Link wallet address"),
    STATUS("/status", "Check status");

    private final String command;
    private final String description;

    BotCommand(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

    public static Optional<BotCommand> fromText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Optional.empty();
        }
        
        String trimmedText = text.trim();
        
        if (trimmedText.startsWith(LINK.command)) {
            return Optional.of(LINK);
        }
        
        return Arrays.stream(values())
                .filter(cmd -> cmd != LINK)
                .filter(cmd -> cmd.command.equals(trimmedText))
                .findFirst();
    }

    public static boolean isCommand(String text) {
        return fromText(text).isPresent();
    }

    public static String getAllCommandsHelp() {
        StringBuilder help = new StringBuilder("Available Commands:\n\n");
        for (BotCommand cmd : values()) {
            help.append(cmd.command).append(" - ").append(cmd.description).append("\n");
        }
        return help.toString();
    }
}
