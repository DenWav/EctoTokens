EctoTokens
==========

EctoTokens is an easily extendable tokens system for CraftBukkit. The tokens are received through a variety of manners,
mainly through in-game actions such as killing mobs. It can be easily extended to add more ways to receive tokens as
well. It is heavily config-file based and creates an inventory GUI from the configuration settings so players can buy
things. This plugin comes with various actions available out of the box, but again, actions and buttons can be extended
and added easily to increase the versatility of this plugin.

Compiling
---------

### Need Maven? [Get Maven Here](http://maven.apache.org/download.cgi)

#### JDK 8 is recommended.

First, you need to run [BuildTools](https://www.spigotmc.org/wiki/buildtools/) to create `craftbukkit-1.8.8.jar` and
place it in the root directory of this project (that's the directory this file is in).

After you have done that, run a working copy of this plugin to create the database this plugin uses, and modify the
`pom.xml` to connect to your database. If you don't want to do that, the table creation statements are listed in
`Table.java`, you can enter them manually if you like. After the tables have been created and the `pom.xml` has been
modified so the QueryDSL plugin can connect to the database, build the project. Your IDE will show lots of errors until
you do this, because before the compilation stage several classes are generated dealing with the database.

`mvn clean package`

When that is finished, look in the `target/` folder, and it will have the compiled .jar file.

Developers
----------

[Information on how to extend EctoTokens can be found here.](extending.md)

Further Info
------------

If you have any questions or want to contact me for any reason, you can find me in IRC:

`irc.spi.gt`

I am usually in *#spigot* and *#spigot-dev*, but you can always just message me, username is DemonWav.
If you can't  find me there, you should always be able to message me in `chat.freenode.net`
