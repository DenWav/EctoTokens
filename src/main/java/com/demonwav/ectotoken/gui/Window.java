package com.demonwav.ectotoken.gui;

import org.bukkit.event.inventory.ClickType;

import java.util.List;

public interface Window {

    void createWindow();
    String getTitle();
    int getNumItems();
    int getHeight();
    boolean hasParent();
    Window getParent();
    void click(int slot, ClickType type);
    void close();
    boolean hasNextPage();
    boolean hasPreviousPage();
    void nextPage() throws IndexOutOfBoundsException;
    void previousPage() throws IndexOutOfBoundsException;
    int getNumPages();
    int getCurrentPage();
    void setPage(int index) throws IndexOutOfBoundsException;
    Page getPage(int index);
    Page getPage();
    List<? extends Page> getPages();
    void updateActionBar();
}
