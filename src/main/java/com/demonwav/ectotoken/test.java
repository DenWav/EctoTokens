package com.demonwav.ectotoken;

import com.demonwav.ectotoken.button.Button;
import com.demonwav.ectotoken.button.EctoButton;
import com.demonwav.ectotoken.gui.EctoWindow;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class test implements CommandExecutor {

    private EctoToken plugin;

    public test(EctoToken plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = ((Player) commandSender);

        ArrayList<Button> items = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 165; i++) {
            items.add(new EctoButton(ChatColor.RED + "Test Button", null, random.nextInt(300), (short)0, null, 0,
                Collections.singletonList((window, player1, plugin1) ->
                    player1.sendMessage(ChatColor.AQUA + "You clicked a button")
                )));
        }

        EctoWindow window = new EctoWindow(plugin.getWindowConfig().getWindow().getTitle(), plugin.getMainConfig().getGui().getHeight(), items, null, player, plugin);
        window.createWindow();


        return true;
    }
}
