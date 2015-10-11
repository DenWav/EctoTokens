package com.demonwav.ectotoken.gui;

import com.demonwav.ectotoken.EctoToken;
import com.demonwav.ectotoken.TokensManager;
import com.demonwav.ectotoken.button.Button;
import com.demonwav.ectotoken.util.StringUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class EctoWindow implements Window {

    private final EctoToken plugin;
    private final Player player;
    private final String baseTitle;
    private final int h;
    private final List<EctoPage> pages;
    private final Window parent;
    private final int leftSlot;
    private final int rightSlot;
    private int currentPage = 0;
    private Inventory inv;

    public EctoWindow(String baseTitle, int h, List<Button> items, Window parent, Player player, EctoToken plugin) {
        this.baseTitle = baseTitle;
        this.plugin = plugin;
        this.player = player;
        this.h = h >= 2 ? h : 2;

        // 2 slots are reserved for width (for page navigation)
        int slots = 7 * (this.h - 1);
        int numPages = (items.size() / slots) + 1;
        ArrayList<EctoPage> pages = new ArrayList<>();
        Iterator<Button> iterator = items.iterator();
        for (int i = 0; i < numPages; i++) {
            ArrayList<Button> tempItems = new ArrayList<>();
            for (int j = 0; j < slots; j++) {
                if (iterator.hasNext())
                    tempItems.add(iterator.next());
                else
                    break;
            }
            pages.add(new EctoPage(this.h, tempItems));
            if (!iterator.hasNext())
                break;
        }
        this.pages = pages;
        this.parent = parent;

        leftSlot = (this.h / 2) * 9;
        rightSlot = ((this.h / 2) * 9) + 8;
    }

    @Override
    public void createWindow() {
        inv = Bukkit.createInventory(new EctoInventoryHolder(this), 9 * h, getTitle());
        // current page is 0 at start, always
        setNavButtons();
        pages.get(currentPage).applyPage(inv);
        player.openInventory(inv);

        final UUID uuid = player.getUniqueId();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            final long tokens = TokensManager.getInstance().getBalance(uuid);
            final String name = player.getName();
            final String text = plugin.getMainConfig().getGui().getActionBar().getStaticTokensView();

            plugin.getServer().getScheduler().runTask(plugin, () ->
                ActionBarManager.getInstance().persistMessage(player, StringUtil.staticTokensViewVar(text, name, tokens))
            );
        });
    }

    @Override
    public void updateActionBar() {
        final UUID uuid = player.getUniqueId();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            final long tokens = TokensManager.getInstance().getBalance(uuid);
            final String name = player.getName();
            final String text = plugin.getMainConfig().getGui().getActionBar().getStaticTokensView();

            plugin.getServer().getScheduler().runTask(plugin, () ->
                    ActionBarManager.getInstance().persistMessage(player, StringUtil.staticTokensViewVar(text, name, tokens))
            );
        });
    }

    @Override
    public String getTitle() {
        return StringUtil.windowTitleVar(baseTitle, player.getName(), currentPage + 1, getNumPages());
    }

    @Override
    public boolean hasNextPage() {
        return currentPage < pages.size() - 1;
    }

    @Override
    public boolean hasPreviousPage() {
        return currentPage > 0;
    }

    @Override
    public void nextPage() throws IndexOutOfBoundsException {
        currentPage++;
        createWindow();
    }

    @Override
    public void previousPage() throws IndexOutOfBoundsException {
        currentPage--;
        createWindow();
    }

    @Override
    public int getNumPages() {
        return pages.size();
    }

    @Override
    public int getCurrentPage() {
        return currentPage;
    }

    @Override
    public void setPage(int index) throws IndexOutOfBoundsException {
        currentPage = index;
        createWindow();
    }

    @Override
    public int getNumItems() {
        int result = 0;
        for (EctoPage page : pages) {
            result += page.getNumItems();
        }
        return result;
    }

    @Override
    public EctoPage getPage(int index) throws IndexOutOfBoundsException {
        return pages.get(index);
    }

    @Override
    public EctoPage getPage() {
        return pages.get(currentPage);
    }

    @Override
    public List<EctoPage> getPages() {
        return pages;
    }

    @Override
    public int getHeight() {
        return h;
    }

    private void setNavButtons() {
        inv.setItem(0, plugin.getMainConfig().getGui().getBackButton().getButton().getItem());
        inv.setItem(8, plugin.getMainConfig().getGui().getCloseButton().getButton().getItem());
        int row = h / 2;
        inv.setItem(row * 9, plugin.getMainConfig().getGui().getLeftNavButton().getButton().getItem());
        inv.setItem(row * 9 + 8, plugin.getMainConfig().getGui().getRightNavButton().getButton().getItem());
    }

    @Override
    public boolean hasParent() {
        return parent != null;
    }

    @Override
    public Window getParent() {
        return parent;
    }

    @Override
    public void click(int slot, ClickType type) {
        if (slot == 0)
            plugin.getMainConfig().getGui().getBackButton().getButton().onClick(this, player, plugin);
        else if (slot == 8)
            plugin.getMainConfig().getGui().getCloseButton().getButton().onClick(this, player, plugin);
        else if (slot == leftSlot)
            plugin.getMainConfig().getGui().getLeftNavButton().getButton().onClick(this, player, plugin);
        else if (slot == rightSlot)
            plugin.getMainConfig().getGui().getRightNavButton().getButton().onClick(this, player, plugin);
        else if (type == ClickType.SHIFT_LEFT)
            getPage().click(slot, player, this, plugin);
        else
            ActionBarManager.getInstance().setActionBarMomentarily(player, ChatColor.LIGHT_PURPLE + "Shift Click to Buy", 3);
    }

    @Override
    public void close() {
        ActionBarManager.getInstance().stopPersist(player);
    }
}
