package com.demonwav.ectotoken;

import com.demonwav.ectotoken.config.main.MySQLConfig;
import com.demonwav.ectotoken.querydsl.QPlayers;
import com.demonwav.ectotoken.util.StringUtil;
import com.demonwav.ectotoken.util.Util;
import com.google.common.collect.Maps;
import com.mysema.query.sql.MySQLTemplates;
import com.mysema.query.sql.RelationalPath;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.dml.SQLDeleteClause;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;

import org.bukkit.OfflinePlayer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;

public class DatabaseManager {
    @Getter
    private static DatabaseManager instance;

    private final EctoToken plugin;
    private String url;
    private Connection connection;

    public DatabaseManager(EctoToken plugin) {
        instance = this;
        this.plugin = plugin;
        getUrl();
        connect();
    }


    public void reload() {
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        getUrl();
        connect();
    }

    private void connect() {
        MySQLConfig config = plugin.getMainConfig().getMysql();
        try {
            connection = DriverManager.getConnection(url, config.getUsername(), config.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().severe(StringUtil.red(" *** EctoToken was unable to communicate with the database,"));
            plugin.getLogger().severe(StringUtil.red(" *** please check your settings and reload, EctoToken will"));
            plugin.getLogger().severe(StringUtil.red(" *** now be disabled."));
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }

    private void getUrl() {
        MySQLConfig config = plugin.getMainConfig().getMysql();
        url = "jdbc:mysql://" + config.getHostname() + ":" + config.getPort() + "/" + config.getDatabase() + "?autoReconnect=true";
    }

    public void setupDatabase() {
        for (Table table : Table.values()) {
            checkTable(table);
            if (!plugin.isEnabled())
                break;
        }
        if (plugin.isEnabled())
            plugin.getLogger().info("Database verified successfully.");
    }

    private void checkTable(Table table) {
        PreparedStatement intPst = null;
        ResultSet resultSet = null;
        ResultSet intResultSet = null;
        try (PreparedStatement pst =
                 connection.prepareStatement("SELECT ENGINE FROM information_schema.TABLES where TABLE_NAME = ?;")) {
            pst.setString(1, table.getName());
            try {
                // This can fail because there is no table
                resultSet = pst.executeQuery();
            } catch (SQLException e) {
                remakeTable(table, false);
            }

            if (resultSet == null) {
                remakeTable(table, false);
                return;
            }

            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, plugin.getMainConfig().getMysql().getDatabase() + "." + table.getName(), null);
            if (tables.next() && resultSet.next()) {
                // Table exists
                // Make sure the engine is correct
                if (!resultSet.getString("ENGINE").equals("InnoDB")) {
                    plugin.getLogger().warning(table.getName() + " is using an incorrect engine.");
                    remakeTable(table, true);
                } else {
                    // Make sure the columns are correct
                    intPst = connection.prepareStatement(String.format("SHOW COLUMNS FROM %s;", table.getName()));
                    intResultSet = intPst.executeQuery();

                    // Make sure there are columns
                    if (intResultSet.last()) {
                        // Make sure there is the correct number of columns
                        if (intResultSet.getRow() != table.getColumns()) {
                            plugin.getLogger().warning(table.getName() + " has an incorrect number of columns.");
                            remakeTable(table, true);
                        } else {
                            // Make sure the columns are correct
                            intResultSet.beforeFirst();
                            for (String column : table.getColumnNames()) {
                                intResultSet.next();
                                if (!intResultSet.getString("Field").equals(column)) {
                                    plugin.getLogger().warning(table.getName() + " has incorrect columns.");
                                    remakeTable(table, true);
                                    break;
                                }
                            }
                        }
                    } else {
                        plugin.getLogger().warning(table.getName() + " has no columns");
                        remakeTable(table, true);
                    }
                }
            } else {
                // Table does not exist
                remakeTable(table, false);
            }
            tables.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resultSet
            try {
                if (resultSet != null)
                    resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Close intPst
            try {
                if (intPst != null)
                    intPst.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Close intResultSet
            try {
                if (intResultSet != null)
                    intResultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void remakeTable(Table table, boolean ask) {
        if (!ask) {
            dropTable(table);
            createTable(table);
            plugin.getLogger().info("Created table `" + table.getName() + "`.");
        } else {
            if (plugin.getMainConfig().getMysql().getForceSetup()) {
                dropTable(table);
                createTable(table);
                plugin.getLogger().info("Created table `" + table.getName() + "`.");
            } else {
                plugin.getLogger().severe(
                    StringUtil.red("*** " + table.getName() + " is not set up correctly and conflicts with EctoToken's setup."));
                plugin.getLogger().severe(
                    StringUtil.red("*** Change mysql.forceSetup, remove or rename this table, or create a new database for"));
                plugin.getLogger().severe(
                    StringUtil.red("*** StatCraft to use. StatCraft will not run unless there are no conflicting tables."));
                plugin.getLogger().severe(
                    StringUtil.red("*** StatCraft will now be disabled."));
                plugin.getPluginLoader().disablePlugin(plugin);
            }
        }
    }

    private void dropTable(Table table) {
        try (PreparedStatement pst = connection.prepareStatement(
            String.format("DROP TABLE IF EXISTS %s;", table.getName()))) {
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable(Table table) {
        try (PreparedStatement pst = connection.prepareStatement(table.getCreate())) {
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPlayerId(UUID uuid) {
        // We will look for the player in the database first
        // If the player doesn't exist, we will attempt to find the name
        // that goes with that UUID and add that entry to the database
        // then return the new id. Will return -1 if all attempts fail.

        byte[] array = Util.UUIDToByte(uuid);
        SQLQuery query = getNewQuery();
        Integer res = query
            .from(QPlayers.players)
            .where(QPlayers.players.uuid.eq(array))
            .uniqueResult(QPlayers.players.id);

        if (res == null) {
            String name;
            OfflinePlayer player = plugin.getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                name = plugin.getServer().getPlayer(uuid).getName();
            } else {
                try {
                    name = getCurrentName(uuid);
                } catch (Exception e) {
                    // something went wrong
                    e.printStackTrace();
                    return -1;
                }
            }

            QPlayers p = QPlayers.players;
            SQLInsertClause clause = getInsertClause(p);
            clause
                .columns(p.uuid, p.name)
                .values(array, name)
                .execute();

            SQLQuery newQuery = getNewQuery();
            res = newQuery
                .from(p)
                .where(p.uuid.eq(array))
                .uniqueResult(p.id);

            return res == null ? -1 : res;
        } else {
            return res;
        }
    }

    public int getPlayerId(String name) {
        // We will look for the name in the database. If it isn't there we will attempt to figure out
        // what the UUID should be by first checking if that player is logged in, and if that fails,
        // then checking the Mojang API. Will return -1 if all attempts fail.

        SQLQuery query = getNewQuery();
        QPlayers p = QPlayers.players;
        Integer res = query
            .from(p)
            .where(p.name.eq(name))
            .uniqueResult(p.id);

        if (res == null) {
            String newName;
            UUID uuid;
            OfflinePlayer player = plugin.getServer().getPlayer(name);
            if (player != null && player.isOnline()) {
                newName = name;
                uuid = player.getUniqueId();
            } else {
                // it failed to find a player by that name, so attempt to do a UUID lookup
                try {
                    Map.Entry<UUID, String> entry = getCurrentNameAndUUID(name);
                    newName = entry.getValue();
                    uuid = entry.getKey();
                } catch (Exception e) {
                    e.printStackTrace();
                    return -1;
                }
            }

            byte[] bUuid = Util.UUIDToByte(uuid);

            query = getNewQuery();
            res = query
                .from(p)
                .where(p.uuid.eq(bUuid))
                .uniqueResult(p.id);

            if (res == null) {
                // UUID isn't in the database
                synchronized (this) {
                    SQLInsertClause clause = getInsertClause(p);
                    clause
                        .columns(p.uuid, p.name)
                        .values(bUuid, newName)
                        .execute();

                    SQLQuery newQuery = getNewQuery();
                    res = newQuery
                        .from(p)
                        .where(p.uuid.eq(bUuid))
                        .uniqueResult(p.id);

                    if (res == null) {
                        // something went wrong
                        return -1;
                    } else {
                        return res;
                    }
                }
            } else {
                // fix the UUID / name pairing
                synchronized (this) {
                    SQLUpdateClause clause = getUpdateClause(p);
                    clause
                        .where(p.uuid.eq(Util.UUIDToByte(uuid)))
                        .set(p.name, newName)
                        .execute();
                }
            }

            return res;
        } else {
            return res;
        }
    }

    public UUID getPlayerUUID(int id) {
        SQLQuery query = getNewQuery();
        QPlayers p = QPlayers.players;
        return Util.byteToUUID(query.from(p).where(p.id.eq(id)).uniqueResult(p.uuid));
    }

    public String getPlayerName(int id) {
        SQLQuery query = getNewQuery();
        QPlayers p = QPlayers.players;
        return query.from(p).where(p.id.eq(id)).uniqueResult(p.name);
    }

    private String get(String url) {
        HttpURLConnection conn;
        BufferedReader rd = null;
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rd != null) {
                try {
                    rd.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    private Map.Entry<UUID, String> getCurrentNameAndUUID(String oldName) {
        final String url = "https://api.mojang.com/users/profiles/minecraft/" + oldName;
        final String s = get(url);

        if (s.isEmpty()) {
            throw new IllegalStateException("No response from Mojang API.");
        }
        Object json = JSONValue.parse(s);
        if (!(json instanceof JSONObject))
            throw new IllegalStateException("Mojang API returned incorrect JSON: " + s);
        JSONObject name = (JSONObject) json;

        if (name.isEmpty()) {
            throw new IllegalStateException("Mojang API returned bad name object: " + s);
        }

        Object finName = name.get("name");
        Object finUUID = name.get("id");
        if (!(finName instanceof String)) {
            throw new IllegalStateException("Mojang API returned bad name object: " + s);
        }
        if (!(finUUID instanceof String)) {
            throw new IllegalStateException("Mojang API returned bad name object: " + s);
        }

        String n = (String) finName;
        if (n.isEmpty()) {
            throw new IllegalStateException("Mojang API returned bad name: " + s);
        }
        UUID u;
        try {
            u = UUID.fromString(((String) finUUID).replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
        } catch (Exception e) {
            throw new IllegalStateException("Mojang API returned bad uuid: " + s);
        }

        return Maps.immutableEntry(u, n);
    }

    private String getCurrentName(UUID uuid) {
        // Get JSON from Mojang API
        final String url = "https://api.mojang.com/user/profiles/" + uuid.toString().replaceAll("-", "") + "/names";
        final String s = get(url);
        if (s.isEmpty()) {
            throw new IllegalStateException("No response from Mojang API.");
        }
        Object list = JSONValue.parse(s);
        if (!(list instanceof JSONArray))
            throw new IllegalStateException("Mojang API returned incorrect JSON: " + s);
        JSONArray names = (JSONArray) list;

        Object last = names.get(names.size() - 1);
        if (!(last instanceof JSONObject))
            throw new IllegalStateException("Mojant API returned incorrect JSON: " + s);

        JSONObject name = (JSONObject) last;
        if (name.isEmpty()) {
            throw new IllegalStateException("Mojang API returned bad name object: " + s);
        }

        Object fin = name.get("name");
        if (!(fin instanceof String)) {
            throw new IllegalStateException("Mojang API returned bad name object: " + s);
        }

        String n = (String) fin;
        if (n.isEmpty()) {
            throw new IllegalStateException("Mojang API returned bad name: " + s);
        }

        return n;
    }

    public SQLQuery getNewQuery() {
        return new SQLQuery(connection, MySQLTemplates.DEFAULT);
    }

    public SQLUpdateClause getUpdateClause(RelationalPath path) {
        return new SQLUpdateClause(connection, MySQLTemplates.DEFAULT, path);
    }

    public SQLInsertClause getInsertClause(RelationalPath path) {
        return new SQLInsertClause(connection, MySQLTemplates.DEFAULT, path);
    }

    public SQLDeleteClause getDeleteCluase(RelationalPath path) {
        return new SQLDeleteClause(connection, MySQLTemplates.DEFAULT, path);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (connection != null)
            connection.close();
    }
}
