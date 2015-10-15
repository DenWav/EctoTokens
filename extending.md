Extending EctoTokens
====================

There are three major areas in terms of extending EctoTokens, and they deal with the GUI: Actions, Buttons, and Windows.
Each of these fits together in a hierarchy, Windows have Buttons, and Buttons can have Actions. Each can have none, or
many of each. They are primarily defined in a user-built YAML config, though you don't necessarily need to define a
config to use them. You do, however, have to define a config to interact with the main Window (the /et shop command).

Maven Setup
-----------

Extending EctoTokens is pretty straightforward with Maven. You can also simply download the jar and use it to build your
plugin, but using Maven is simpler. For the Maven setup, first add the repository:

```xml
<repository>
    <id>demonwav-repo</id>
    <url>http://nexus.demonwav.com/content/repositories/snapshots/</url>
</repository>
```

Once you have done that you can add the EctoTokens plugin as a dependency:

```xml
<dependency>
    <groupId>com.demonwav.ectotoken</groupId>
    <artifactId>ectotoken</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

The scope bit is important, without it you'll shade the EctoTokens plugin in with your plugin. Sources and Javadocs are
also up on the Maven repo, so your IDE should download them automatically.

Now that you have the EctoTokens plugin as a dependency, you'll need to tell Bukkit that you depend on EctoTokens and
when to load your plugin. In your `plugin.yml` add a `depend` and `load` tag:

```yaml
depend: [EctoTokens]
load: STARTUP
```

The brackets around `EctoTokens` simply means it's a list, and it's required even if you only have one element. There
are two options for `load`: POSTWORLD and STARTUP. When omitted it defaults to POSTWORLD. EctoTokens doesn't set this
value, so it loads on POSTWORLD, so STARTUP tells Bukkit to load this plugin before EctoTokens, in a way. This is done
instead of `loadbefore` because `loadbefore` doesn't always work right for some reason. Having this plugin load before
EctoTokens is crucial, because we need to tell EctoTokens how to read our config. The way we do this the same
regardless if we are defining our own Action, Window, or Button. However, to show the specifics on each, they have
been broken up into their own sections.

General Configuration Notes
---------------------------

Everything is powered by the `shop.yml` config in EctoTokens. This tells EctoTokens how to create the GUI and what to
put in it, as well as what to do when the user clicks something in it. This is where you're going to extend the GUI, by
defining your own Actions to run when a user clicks some Button, defining your own Buttons to be displayed in a Window,
and even defining your own Window to be opened as the root window, or opened by the Action of pressing some Button.
These are all distinct things you can extend, but the config side of it is all the same. I'll use an Action to show you
how to define something for a Config, and how to tell the YAML parser what class to look at for your config.

All Config related classes must implement Config. This is so EctoTokens can validate the Config regardless of which
classes are in it. For Actions, Buttons, and Windows, you'll implement a sub-interface of the Config interface. All
Config implementations must be standard JavaBeans - that is, have a public no-argument constructor, all properties of
the class are private with getters and setters of the same name. This is true except for boolean types, which need to
break the JavaBeans standard and use "getValue" rather than "isValue" for the name of the getter method. This is because
YamlBeans doesn't have a special case for boolean values, so can't find the method. To get around this using Lombok,
just use the Boolean class instead. The general contract of classes that implement Config is, if the class contains any
other classes which implement Config, the `validate()` method call of the first class will also call `validate()` on all
children Config implementations. For our Action example, let's implement the `ActionConfig` interface:

```java
import com.demonwav.ectotoken.config.ActionConfig;

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

I'll go over in-detail what the implementing details mean in the specific sections, but the `validate()` method is a
part of all Config implementations. This takes a Logger as a parameter so you can log any errors or warnings with the
configuration the user has given, and then return true or false accordingly. Only return false if there is an actual
error with their configuration, and in that case you should have a `logger.severe()` call somewhere in the method
telling the user what is wrong. For instances where the configuration maybe doesn't make sense or is otherwise weird but
not invalid, use `logger.warn()` to warn the user about it, but continue to return true. **Returning false from this
method will cause the entire configuration to fail and the EctoTokens plugin will not enable.** Don't take that as a
reason to never return false, though. You should only return true if the setup meets the required specifications for
your plugin.

Convenience methods for the `validate()` method are in the
[Configs](src/main/java/com/demonwav/ectotokens/config/Configs.java) class.

So we have created a Config class that defines two properties, `phrase` and `color`. We need the user to define what
these values are in their config file. To do this, the user needs to add the configuration for this Action to one of
their Buttons. `ButtonConfig` expects a list of `ActionConfig`'s as one of it's properties, but the parser will have no
way to tell which implementation of the class to use. So we will need to tell the parser the classpath for our
`ActionConfig` implementation:

```yaml
actions:
  - !com.demonwav.someplugin.SomeConfig
    phrase: Hello World!
    color: 1
```

Just so there is no confusion, that is the `action` property of one of the Buttons defined in the `shop.yml`.

Okay, so we have created an ActionConfig implementation, and created a listing in the EctoTokens configuration file that
points to our implementation, and has the properties for our class. This will work as is (assuming the other side of the
Action has been created, which will be explained down below), but it's ugly and more difficult for the user to remember.
To remedy this, we will tell the parser a special tag that will be marked with this class. The tag can be whatever you
like, but try to not make it so generic it will clash with someone else's tag. The way we set this tag is by calling
the `registerTag()` on the EctoTokens instance in our plugin's onEnable() method:

```java
public class SomePlugin extends JavaPlugin {

    @Override
    final public void onEnable() {
        EctoToken ectoToken = (EctoToken) Bukkit.getPluginManager().getPlugin("EctoToken");
        ectoToken.registerTag("SendPhrase", SomeConfig.class);
    }
}
```

Now we can simplify the action config to:

```yaml
actions:
  - !SendPhrase
    phrase: Hello World!
    color: 1
```

Which is easier to type and remember.

The parser is also not very smart, if you use a container class that contains something other than `String`s, you'll
need to tell the parser what type to expect there as well. The container types you should use are `List` and `Map`.
You shouldn't define them as `ArrayList` or `HashMap`, though, just leave them abstract. Do not use arrays. Let's look
at this example:

```java
import com.demonwav.ectotoken.config.ActionConfig;

public class SomeConfig implements ActionConfig {
    
    private List<Integer> nums;
    
    /* getters and setters */
    
    @Override
    public Action getAction() {
        return new SomeAction(this);
    }
    
    @Override
    public boolean validate(Logger logger) {
        if (phrases == null)
            phrases = Collections.emptyList();
        
        Configs.removeNulls(nums);
        
        if (phrases.isEmpty()) {
            logger.severe("You must set at least one number");
            return false;
        }
        
        return true;
    }
}
```

Here we have a List of Integers the user must define. We state that the List needs to have at least one item in it in
the `validate()` method. For this `ActionConfig` our YAML now looks like this:

```yaml
actions:
  -
    nums:
      - !java.lang.Integer
        1
      - !java.lang.Integer
        2
      - !java.lang.Integer
        3
```

We have to do this for the List because they aren't `String`s, they are `Integer`s. This is obviously not what we want,
so we tell the parser to expect a List of integers instead, using the `register()` method in the EctoTokens instance:

```java
public class SomePlugin extends JavaPlugin {

    @Override
    final public void onEnable() {
        EctoToken ectoToken = (EctoToken) Bukkit.getPluginManager().getPlugin("EctoToken");
        ectoToken.register(SomeConfig.class, "nums", Integer.class, EctoToken.Register.ELEMENT);
    }
}
```

The `SomeConfig.class` is the parent entry, or where this field resides. The next argument is the field name, and then
the class of the field that should be in the List after that. The last argument differentiates between items where the
*elements* of the field should be that type, or the *field itself* should be that type. An example of when the parser
won't know what the type of the field itself might be is when the field is an abstract class or an interface. You would
need to use `EctoToken.Register.DEFAULT` to tell the parser to set that property as whatever class you give it. After
we have added this to our `onEnable()` we no longer need to tell the parser what type each field is:
 
```yaml
actions:
  -
    nums:
      - 1
      - 2
      - 3
```

Tutorials
---------

View tutorials for doing more specific things here:
* [Creating an Action](create_action.md)
* [Creating a Button](create_button.md)
* [Creating a Window](create_window.md)
