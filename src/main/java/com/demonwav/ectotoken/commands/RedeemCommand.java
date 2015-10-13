package com.demonwav.ectotoken.commands;

import com.demonwav.ectotoken.CouponManager;
import com.demonwav.ectotoken.DatabaseManager;
import com.demonwav.ectotoken.EctoToken;
import com.demonwav.ectotoken.Perm;
import com.demonwav.ectotoken.TokensManager;
import com.demonwav.ectotoken.querydsl.Coupon;
import com.demonwav.ectotoken.util.StringUtil;
import com.demonwav.ectotoken.util.Util;

import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class RedeemCommand implements EctoCommand {

    private EctoToken plugin;

    @Override
    public boolean hasPermission(CommandSender sender, String[] args) {
        return sender.hasPermission(Perm.getPlayerRedeem());
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender instanceof OfflinePlayer) {
            if (args.length == 0) {
                sender.sendMessage("Usage: /et redeem <code>");
            } else {
                final String code = Arrays.stream(args).collect(Collectors.joining(" "));

                Util.runTaskAsync(new Runnable() {
                    @Override
                    public void run() {
                        final Coupon coupon = CouponManager.getInstance().getCouponByName(code);
                        if (coupon != null) {
                            int id = DatabaseManager.getInstance().getPlayerId(((OfflinePlayer) sender).getUniqueId());
                            if (id == -1) {
                                Util.runTask(new Runnable() {
                                    @Override
                                    public void run() {
                                        sender.sendMessage(ChatColor.RED + "Something went wrong while trying to process that command");
                                    }
                                });
                                return;
                            }
                            boolean success = CouponManager.getInstance().addCouponUse(id, coupon.getCouponId());
                            if (success) {
                                TokensManager.getInstance().modifyBalance(id, coupon.getAmount(), "COUPON - Code:" + coupon.getCouponName());
                                Util.runTask(new Runnable() {
                                    @Override
                                    public void run() {
                                        sender.sendMessage(ChatColor.GOLD + "Coupon redeemed, awarded " + ChatColor.YELLOW + StringUtil.formatTokens(coupon.getAmount()) + ChatColor.GOLD + " tokens!");
                                    }
                                });
                            } else {
                                Timestamp time = CouponManager.getInstance().getWhenCouponUsed(id, coupon.getCouponId());
                                if (time == null) {
                                    Util.runTask(new Runnable() {
                                        @Override
                                        public void run() {
                                            sender.sendMessage(ChatColor.DARK_PURPLE + "That coupon has expired.");
                                        }
                                    });
                                } else {
                                    final Date date = new Date(time.getTime());
                                    final SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy, hh:mm aa zzz");
                                    format.setTimeZone(Calendar.getInstance().getTimeZone());
                                    Util.runTask(new Runnable() {
                                        @Override
                                        public void run() {
                                            sender.sendMessage(ChatColor.GOLD + "You used that coupon on " + ChatColor.YELLOW + format.format(date) + ChatColor.GOLD + ".");
                                        }
                                    });
                                }
                            }
                        } else {
                            Util.runTask(new Runnable() {
                                @Override
                                public void run() {
                                    sender.sendMessage("That coupon doesn't exist.");
                                }
                            });
                        }
                    }
                });
            }
        } else {
            sender.sendMessage("You must be a player to run this command.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
