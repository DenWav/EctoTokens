package com.demonwav.ectotokens;

import com.demonwav.ectotokens.commands.AddCommand;
import com.demonwav.ectotokens.commands.AdminCommand;
import com.demonwav.ectotokens.commands.BalanceCommand;
import com.demonwav.ectotokens.commands.BaseCommand;
import com.demonwav.ectotokens.commands.GenerateCommand;
import com.demonwav.ectotokens.commands.HelpCommand;
import com.demonwav.ectotokens.commands.RedeemCommand;
import com.demonwav.ectotokens.commands.ReloadCommand;
import com.demonwav.ectotokens.commands.ResetCommand;
import com.demonwav.ectotokens.commands.SetCommand;
import com.demonwav.ectotokens.commands.ShopCommand;
import com.demonwav.ectotokens.commands.TakeCommand;
import com.demonwav.ectotokens.commands.TopCommand;
import com.demonwav.ectotokens.config.Config;
import com.demonwav.ectotokens.config.main.MainConfig;
import com.demonwav.ectotokens.config.shop.EctoButtonConfig;
import com.demonwav.ectotokens.config.shop.EctoWindowConfig;
import com.demonwav.ectotokens.config.shop.MainWindowConfig;
import com.demonwav.ectotokens.config.shop.actions.ApplyEffectsActionConfig;
import com.demonwav.ectotokens.config.shop.actions.EnterLotteryActionConfig;
import com.demonwav.ectotokens.config.shop.actions.GiveItemsActionConfig;
import com.demonwav.ectotokens.config.shop.actions.GiveTokensActionConfig;
import com.demonwav.ectotokens.config.shop.actions.OpenWindowActionConfig;
import com.demonwav.ectotokens.config.shop.actions.RepairItemsActionConfig;
import com.demonwav.ectotokens.config.tokens.LotteryConfig;
import com.demonwav.ectotokens.config.tokens.OreConfig;
import com.demonwav.ectotokens.config.tokens.TokensConfig;
import com.demonwav.ectotokens.events.EctoInventoryEvent;
import com.demonwav.ectotokens.events.KillEvent;
import com.demonwav.ectotokens.gui.ActionBarManager;
import com.demonwav.ectotokens.gui.EctoInventoryHolder;
import com.demonwav.ectotokens.util.StringUtil;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EctoTokens extends JavaPlugin {

    private final List<ConfigReg> registerList = new LinkedList<>();
    private final HashMap<String, Class<?>> tagMap = new HashMap<>();

    @Getter
    private MainConfig mainConfig;
    @Getter
    private MainWindowConfig windowConfig;
    @Getter
    private TokensConfig tokensConfig;

    @Override
    final public void onEnable() {
        // Initialize the managers which needs this plugin instance
        new ActionBarManager(this);
        new LotteryManager(this);

        // Register event listeners
        getServer().getPluginManager().registerEvents(new EctoInventoryEvent(), this);
        getServer().getPluginManager().registerEvents(new KillEvent(this), this);

        // Prepare to load the configs
        registerClasses();
        loadConfigs();

        // check if the config loading failed
        if (!isEnabled())
            return;

        // Connect to the database
        new DatabaseManager(this);

        // check if the database connection failed
        if (!isEnabled())
            return;

        DatabaseManager.getInstance().setupDatabase();

        // Register base command
        BaseCommand baseCommand = new BaseCommand(this);
        getCommand("ectotoken").setExecutor(baseCommand);

        // register sub commands
        baseCommand.registerCommand("help", new HelpCommand(this));
        BalanceCommand command = new BalanceCommand(this);
        baseCommand.registerCommand("balance", command);
        baseCommand.registerCommand("bal", command);
        baseCommand.registerCommand("top", new TopCommand(this));
        baseCommand.registerCommand("shop", new ShopCommand(this));
        baseCommand.registerCommand("redeem", new RedeemCommand(this));

        baseCommand.registerCommand("admin", new AdminCommand(this));
        baseCommand.registerCommand("add", new AddCommand(this));
        baseCommand.registerCommand("set", new SetCommand(this));
        baseCommand.registerCommand("take", new TakeCommand(this));
        baseCommand.registerCommand("reset", new ResetCommand(this));
        baseCommand.registerCommand("generate", new GenerateCommand(this));
        baseCommand.registerCommand("reload", new ReloadCommand(this));

        // Protect against NoClassDefFoundError in onDisable
        new EctoInventoryHolder(null);
    }

    private void copy(String in, File dest) {
        File temp = new File(getDataFolder(), in);
        try {
            FileUtils.copyFile(temp, dest);
        } catch (IOException e) {
            getLogger().severe(StringUtil.red("Something went wrong when trying to copy the template config. Disabling."));
            e.printStackTrace();
            setEnabled(false);
        }
    }

    public void register(Class<?> parent, String name, Class<?> child, Register type) {
        registerList.add(new ConfigReg(parent, name, child, type));
    }

    public void registerTag(String tag, Class<? extends Config> type) {
        tagMap.put(tag, type);
    }

    private void registerClasses() {
        // Set up the class property stuff
        register(LotteryConfig.class, "winningOptions", LotteryConfig.WinningOption.class, Register.ELEMENT);
        register(TokensConfig.class, "ores", OreConfig.class, Register.ELEMENT);
        register(TokensConfig.class, "lotteries", LotteryConfig.class, Register.ELEMENT);
        register(ApplyEffectsActionConfig.class, "effects", ApplyEffectsActionConfig.Effect.class, Register.ELEMENT);
        register(GiveItemsActionConfig.Item.class, "enchants", GiveItemsActionConfig.Item.Enchant.class, Register.ELEMENT);
        register(GiveItemsActionConfig.class, "items", GiveItemsActionConfig.Item.class, Register.ELEMENT);
        register(RepairItemsActionConfig.class, "itemTypes", Integer.class, Register.ELEMENT);
        register(OpenWindowActionConfig.class, "window", EctoWindowConfig.class, Register.DEFAULT);
        register(MainWindowConfig.class, "window", EctoWindowConfig.class, Register.DEFAULT);
        register(EctoWindowConfig.class, "buttons", EctoButtonConfig.class, Register.ELEMENT);

        // Setup the tag shortcuts
        registerTag("ApplyEffects", ApplyEffectsActionConfig.class);
        registerTag("EnterLottery", EnterLotteryActionConfig.class);
        registerTag("GiveItems", GiveItemsActionConfig.class);
        registerTag("GiveTokens", GiveTokensActionConfig.class);
        registerTag("OpenWindow", OpenWindowActionConfig.class);
        registerTag("RepairItems", RepairItemsActionConfig.class);
    }

    private void setRegisteredClasses(YamlConfig config) {
        for (ConfigReg configReg : registerList) {
            if (configReg.getType() == Register.DEFAULT) {
                config.setPropertyDefaultType(configReg.getParent(), configReg.getName(), configReg.getChild());
            } else {
                config.setPropertyElementType(configReg.getParent(), configReg.getName(), configReg.getChild());
            }
        }
        for (Map.Entry<String, Class<?>> entry : tagMap.entrySet()) {
            config.setClassTag(entry.getKey(), entry.getValue());
        }
    }

    public void loadConfigs() {
        loadMainConfig();
        loadShopConfig();
        loadTokensConfig();

        if (!isEnabled()) {
            getLogger().severe(StringUtil.red("There was an error while attempting to load the configs, and the plugin has been disabled."));
        }
    }

    public void loadMainConfig() {
        getLogger().info("Loading and verifying main config...");
        mainConfig = loadConfig("config.temp.yml", "config.yml", MainConfig.class);
        checkConfig(mainConfig, "Main");
    }

    public void loadShopConfig() {
        getLogger().info("Loading and verifying shop config...");
        windowConfig = loadConfig("shop.temp.yml", "shop.yml", MainWindowConfig.class);
        checkConfig(windowConfig, "Shop");
    }

    public void loadTokensConfig() {
        getLogger().info("Loading and verifying tokens config...");
        tokensConfig = loadConfig("tokens.temp.yml", "tokens.yml", TokensConfig.class);
        checkConfig(tokensConfig, "Tokens");
    }

    private void checkConfig(Config config, String name) {
        if (config == null || !config.validate(getLogger())) {
            getLogger().severe(StringUtil.red(name + " config was not verified successfully."));
            setEnabled(false);
        } else {
            getLogger().info(name + " config was verified successfully.");
        }
    }

    private <T> T loadConfig(String name, String userName, Class<T> clazz) {
        saveResource(name, true);
        File userFile = new File(getDataFolder(), userName);

        if (!userFile.exists()) {
            copy(name, userFile);
        }

        T t = null;
        try (FileReader fileReader = new FileReader(userFile)) {
            YamlReader reader = new YamlReader(fileReader);
            setRegisteredClasses(reader.getConfig());
            t = reader.read(clazz);
            reader.close();
        } catch (FileNotFoundException | YamlException e) {
            getLogger().severe("Something went wrong while trying to read the config. Disabling.");
            e.printStackTrace();
            setEnabled(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }

    @Override
    final public void onDisable() {
        for (Player player : getServer().getOnlinePlayers()) {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof EctoInventoryHolder ||
                player.getOpenInventory().getBottomInventory().getHolder() instanceof EctoInventoryHolder) {
                player.closeInventory();
            }
        }

        if (DatabaseManager.getInstance() != null)
            DatabaseManager.getInstance().close();
    }

    //\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
    //       CONFIG REGISTRATION START        //
    //\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
    public enum Register {
        DEFAULT, ELEMENT
    }

    @Data
    @RequiredArgsConstructor
    private class ConfigReg {

        private final Class<?> parent;
        private final String name;
        private final Class<?> child;
        private final Register type;
    }
    //\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
    //       CONFIG REGISTRATION END          //
    //\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/\/
}
