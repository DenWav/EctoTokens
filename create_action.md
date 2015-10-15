Creating an Action in EctoTokens
================================

Actions can be defined on instances of `EctoButton`, the default `Button` implementation. Not all `Button`
implementations need or will accept actions, but if you're just using the default `EctoButton`, then you only need to
create an `Action` for it, rather than creating a whole button.
There are two parts to creating an action: implementing `ActionConfig`, and extending `Action`. First, we will implement
`ActionConfig`:

```java
import com.demonwav.ectotokens.config.ActionConfig;

public class SomeConfig implements ActionConfig {
    
    private String phrase;
    private String color;
    
    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }
    
    public String getPhrase() {
        return phrase;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getColor() {
        return color;
    }
    
    @Override
    public Action getAction() {
        return new SomeAction(this);
    }
    
    @Override
    public boolean validate(Logger logger) {
        boolean result = true;
        if (phrase == null || phrase.trim().isEmpty()) {
            logger.severe("phrase must be set");
            result = false;
        }
        if (color == null || color.trim().isEmpty()) {
            logger.severe("color must be set");
            result = false;
        }
        return result;
    }
}
```

and register this `ActionConfig` type with a tag:

```java
public class SomePlugin extends JavaPlugin {

    @Override
    final public void onEnable() {
        EctoTokens ectoTokens = (EctoTokens) Bukkit.getPluginManager().getPlugin("EctoTokens");
        ectoTokens.registerTag("SendPhrase", SomeConfig.class);
    }
}
```

We can actually make this significantly smaller if we simply use the `@Data` annotation from Lombok:

```java
import com.demonwav.ectotokens.config.ActionConfig;

@Data
public class SomeConfig implements ActionConfig {
    
    private String phrase;
    private String color;
    
    @Override
    public Action getAction() {
        return new SomeAction(this);
    }
    
    @Override
    public boolean validate(Logger logger) {
        boolean result = true;
        if (phrase == null || phrase.trim().isEmpty()) {
            logger.severe("phrase must be set");
            result = false;
        }
        if (color == null || color.trim().isEmpty()) {
            logger.severe("color must be set");
            result = false;
        }
        return result;
    }
}
```

`getAction()` is a method defined in the `ActionConfig` interface. This is how EctoTokens transforms this configuration
setup to some Action to be run. You can use whatever logic you like to define your Action implementation, but here we
just give the Action this instance of the config class, and let the Action decide what to do based on the values in the
config. We have created an implementation for `ActionConfig`, but we need an `Action` if we want to actually do
something:

```java
import com.demonwav.ectotokens.action.Action;

public class SomeAction extends Action {

    private SomeConfig config;

    public SomeAction(SomeConfig config) {
        this.config = config;
    }

    @Override
    public void run(Window window, Player player, EctoTokens ectoTokens) {
        player.sendMessage(ChatColor.getByChar(config.getColor()) + config.getPhrase());
    }
}
```

This is a simple Action, when we are ran we simply send the phrase to the player in whatever color the player
set in the config. Now we have the Config and the Action defined, we need to add it to our `shop.yml`:

```yaml
window:
  title: '&6 Store &e| &6Page &4{pagenumber} / {totalpages}'
  buttons:
    -
      title: Example Button
      desc:
        - First line example
        - Second line example
      showPrice: false
      price: 0
      itemId: 2
      itemData: 0
      permission: ''
      actions:
        - !SendPhrase
          word: Hello World!
          color: 1
```

If the action can fail for some reason, say it requires room in their inventory and they don't have an empty slot, you
can override the `checkAction()` method to make sure the Action would succeed if it were run at this time. By default
you don't need to check the action if it can't fail, as `checkAction()` will always return true.

And that's all there is to it! You now have a custom-made Action that can be applied to any EctoButton.
