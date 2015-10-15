Creating a Window in EctoTokens
===============================

Windows are the root of the GUI setup in EctoTokens. They can be the first thing to open after the `/et shop` command,
or opened by an action on a button. There are two parts to creating a window: implementing `WindowConfig`, and
implementing `Window`. First, we will implement `WindowConfig`:

```java
import com.demonwav.ectotoken.config.WindowConfig;

public class SomeConfig implements WindowConfig {

    private String windowTitle;
    
    public void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }
    
    public String getWindowTitle() {
        return windowTitle;
    }

    @Override
    public boolean validate(Logger logger) {
        if (windowTitle == null || windowTitle.trim().isEmpty()) {
            logger.severe("windowTitle must be set");
            return false;
        }
        return true;
    }

    @Override
    public String getTitle() {
        return windowTitle;
    }

    @Override
    public Window getWindow(int height, Window parent, Player player, EctoToken plugin) {
        SomePlugin somePlugin = (SomePlugin) Bukkit.getPluginManager().getPlugin("SomePlugin");
        return new SomeWindow(height, parent, player, plugin, windowTitle, somePlugin);
    }
}
```

and register the `WindowConfig` type with a tag:

```java
public class SomePlugin extends JavaPlugin {

    @Override
    final public void onEnable() {
        EctoToken ectoToken = (EctoToken) Bukkit.getPluginManager().getPlugin("EctoToken");
        ectoToken.registerTag("SomeWindow", SomeConfig.class);
    }
}
```

We can actually make this significantly smaller if we simply use the `@Data` annotation from Lombok:

```java
import com.demonwav.ectotoken.config.WindowConfig;

@Data
public class SomeConfig implements WindowConfig {

    private String windowTitle;

    @Override
    public boolean validate(Logger logger) {
        if (windowTitle == null || windowTitle.trim().isEmpty()) {
            logger.severe("windowTitle must be set");
            return false;
        }
        return true;
    }

    @Override
    public String getTitle() {
        return windowTitle;
    }

    @Override
    public Window getWindow(int height, Window parent, Player player, EctoToken plugin) {
        SomePlugin somePlugin = (SomePlugin) Bukkit.getPluginManager().getPlugin("SomePlugin");
        return new SomeWindow(height, parent, player, plugin, windowTitle, somePlugin);
    }
}
```

`getTitle()` and `getWindow()` are methods defined in the `WindowConfig` interface. This is how EctoTokens transforms
this configuration setup to some Window to open. You can use whatever logic you like to define you Window implementation,
but here we just pass along the provided values and throw in our plugin too. Here's a rundown of the arguments:

* height: Global height setting defined in EctoTokens' `config.yml`.
* parent: If an Action opened this Window then this has a parent Window that it will need to reopen if it needs to go "back". This may be null.
* player: The player this Window is for.
* plugin: The instance of the EctoTokens plugin.

We have created an implementation of `Window`, but we need a `Window` if we want the window to actually open:

```java
import com.demonwav.ectotoken.gui.EctoInventoryHolder;
import com.demonwav.ectotoken.gui.Window;

public class SomeWindow implements Window {

    private int height;
    private Window parent;
    private Player player;
    private EctoToken ectoToken;
    private String title;
    private SomePlugin plugin;

    public SomeWindow(int height, Window parent, Player player, EctoToken ectoToken, String title, SomePlugin plugin) {
        this.height = height;
        this.parent = parent;
        this.player = player;
        this.ectoToken = ectoToken;
        this.title = title;
        this.plugin = plugin;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void createWindow() {
        Inventory inv = Bukkit.createInventory(new EctoInventoryHolder(this), 9 * height, getTitle());

        player.openInventory(inv);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getNumItems() {
        return 0;
    }

    @Override
    public int getHeight() {
        return height;
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
        // do nothing
    }

    @Override
    public void close() {
        // do nothing
    }

    @Override
    public boolean hasNextPage() {
        return false;
    }

    @Override
    public boolean hasPreviousPage() {
        return false;
    }

    @Override
    public void nextPage() throws IndexOutOfBoundsException {

    }

    @Override
    public void previousPage() throws IndexOutOfBoundsException {

    }

    @Override
    public int getNumPages() {
        return 0;
    }

    @Override
    public int getCurrentPage() {
        return 0;
    }

    @Override
    public void setPage(int index) throws IndexOutOfBoundsException {
        // do nothing
    }

    @Override
    public Page getPage(int index) {
        return null;
    }

    @Override
    public Page getPage() {
        return null;
    }

    @Override
    public List<? extends Page> getPages() {
        return null;
    }

    @Override
    public void updateActionBar() {
        // do nothing
    }
}
```

This is a simple Window, it does absolutely nothing but show an inventory with a title. It has no actions. Normally
you would want to implement the `Page` interface, or use the default implementation, `EctoPage`, as all Windows in
EctoTokens are collections of pages. As just an example here, we will do nothing at all. As this is a lot to implement
and you may only want to change the default behavior of one aspect of the default window you may want to extend
`EctoWindow` instead, and only override the methods you want to change.

The `EctoInventoryHolder` is a convenience class provided by EctoTokens. If you create an inventory which has this class
as the InventoryHolder, then you won't need to set any events up, EctoTokens will automatically send clicks to your
Window and call the `close()` method when the inventory has been closed. You don't actually need to do anything in the
`close()` method besides clean-up anything that needs it, such as in the `EctoWindow` implementation, the
`ActionBarManager` needs to be told to stop sending action bar updates when the inventory closes. You don't actually
need to close the inventory with this method - this method is called after that has happened.

The `createWindow()` method is where the inventory is created for the player. Any time you want to update the inventory
title you will need to call this method again, as the title of an inventory cannot be changed without re-creating it.

If you are defining your own implementation of `Window` then all of the convenience features of having the GUI buttons
be provided for you and such won't be there, so you'll need to define them yourself if you want them.

Now we have the Config and the Window defined, we need to add it to our `shop.yml`:

```yaml
window: !SomeWindow
  windowTitle: 'Hello World!'
```

Notice that, unlike normal, you need to tell the parser the type tag for this Window. That is because EctoTokens already 
tells the parser to expect this to be an `EctoWindow`, so there is no need to explicitly tell it. Whenever you define a 
new Window that isn't an `EctoWindow`, you need to tell it by adding the type tag.

And that's all there is to it! You now have a custom-made Window.
