package com.demonwav.ectotoken.commands;

import com.demonwav.ectotoken.DatabaseManager;
import com.demonwav.ectotoken.EctoToken;
import com.demonwav.ectotoken.Perm;
import com.demonwav.ectotoken.querydsl.QBalance;
import com.demonwav.ectotoken.util.StringUtil;
import com.demonwav.ectotoken.util.Util;

import com.mysema.query.Tuple;
import com.mysema.query.sql.SQLQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class TopCommand implements EctoCommand {

    private EctoToken plugin;

    @Override
    public boolean hasPermission(CommandSender sender, String[] args) {
        return sender.hasPermission(Perm.getPlayerHelp());
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        Util.runTaskAsync(new Runnable() {
            @Override
            public void run() {
                SQLQuery query = DatabaseManager.getInstance().getNewQuery();
                QBalance b = QBalance.balance;

                // Get the data from the DB, and convert it into a list of Info objects
                List<Tuple> list = query.from(b).orderBy(b.amount.desc()).limit(10).list(b.id, b.amount);
                final List<Info> info = new ArrayList<>();
                for (Tuple tuple : list) {
                    info.add(Info.fromTuple(tuple));
                }

                // Sum up the total server amount
                query = DatabaseManager.getInstance().getNewQuery();
                Long total = query.from(b).uniqueResult(b.amount.sum());
                total = total == null ? 0 : total;
                // needed for the lambda
                final long finalTotal = total;
                Util.runTask(new Runnable() {
                    @Override
                    public void run() {
                        String headerText = plugin.getMainConfig().getCommands().getTop().getHeader();
                        sender.sendMessage(StringUtil.topHeaderTextVar(headerText, finalTotal));
                        final String lineText = plugin.getMainConfig().getCommands().getTop().getLines();
                        for (int i = 0; i < info.size(); i++) {
                            sender.sendMessage(
                                StringUtil.topLineTextHeader(
                                    lineText,
                                    info.get(i).getName(),
                                    info.get(i).getAmount(),
                                    i + 1
                                )
                            );
                        }
                    }
                });
            }
        });
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}

@Data
class Info {
    private String name;
    private long amount;

    public static Info fromTuple(Tuple tuple) {
        Integer id = tuple.get(QBalance.balance.id);
        if (id == null)
            return null;

        String name = DatabaseManager.getInstance().getPlayerName(id);
        Long amount = tuple.get(QBalance.balance.amount);

        Info info = new Info();
        info.setName(name);
        info.setAmount(amount == null ? 0 : amount);
        return info;
    }
}
