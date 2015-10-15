Creating a Button in EctoTokens
===============================

Buttons can be defined on instances of `EctoWindow`, the default `Window` implementation. Not all `Window`
implementations need or will accept buttons, but if you're just using the default `EctoWindow`, then you only need to
create a `Button` for it, rather than creating a whole Window.
There are two parts to creating a button: implementing `ButtonConfig`, and extending `Button`. First, we will implement
`ButtonConfig`:

```java
import com.demonwav.ectotokens.config.ButtonConfig;

public class SomeConfig implements ButtonConfig {
    
    private int typeId;
    
    public void setTypeId(int typeId() {
        this.typeId = typeId;
    }
    
    public int getTypeId() {
        return typeId;
    }
    
    @Override
        public Button getButton() {
            return new SomeButton(this);
        }
    
        @Override
        public boolean validate(Logger logger) {
            return true;
        }
}
```

and register this `ButtonConfig` type with a tag:

```java
public class SomePlugin extends JavaPlugin {

    @Override
    final public void onEnable() {
        EctoToken ectoToken = (EctoToken) Bukkit.getPluginManager().getPlugin("EctoToken");
        ectoToken.registerTag("SomeButton", SomeConfig.class);
    }
}
```

We can actually make this significantly smaller if we simply use the `@Data` annotation from Lombok:

```java
import com.demonwav.ectotokens.config.ButtonConfig;

@Data
public class SomeConfig implements ButtonConfig {
    
    private int typeId;
    
    @Override
    public Button getButton() {
        return new SomeButton(this);
    }

    @Override
    public boolean validate(Logger logger) {
        return true;
    }
}
```

`getButton()` is a method defined in the `ButtonConfig` interface. This is how EctoTokens transforms this configuration
setup to some Button to show. You can use whatever logic you like to define your Button implementation, but here we just
give the Action this instance of the config class, and let the Button decided what to do based on the values in the
config. We have created an implementation of `ButtonConfig`, but we need a `Button` if we want to actually do something:

```java
import com.demonwav.ectotoken.button.Button;

public class SomeButton extends Button {

    private SomeConfig config;

    public SomeButton(SomeConfig config) {
        this.config = config;
    }

    @Override
        public ItemStack getItem() {
            ItemStack stack = new ItemStack(config.getTypeId(), 1);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName("Hello World!");
            stack.setItemMeta(meta);
            return stack;
        }

    @Override
    public void onClick(Window window, Player player, EctoToken plugin) {
        // do nothing
    }
}
```

This is a simple Button, when the button is clicked it will simply do nothing. The button is a block or item with the
id defined in the config, and it's display text is "HelloWorld!". Now we have the Config and Button defined, we need
to add it to our `shop.yml`:

```yaml
window:
  title: '&6 Store &e| &6Page &4{pagenumber} / {totalpages}'
  buttons:
    - !SomeButton
      typeId: 1
```

Notice that, unlike normal, you need to tell the parser the type tag for this Button. That is because EctoTokens already
tells the parser to expect this list to contain `EctoButton` implementations, so there is no need for it to be there at
all. whenever you define a new Button that isn't an `EctoButton`, you need to tell it by adding the type tag.

And that's all there is to it! You now have a custom-made Button that can be applied to any EctoWindow.
