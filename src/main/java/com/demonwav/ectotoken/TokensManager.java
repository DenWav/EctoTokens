package com.demonwav.ectotoken;

import com.demonwav.ectotoken.querydsl.QBalance;
import com.demonwav.ectotoken.querydsl.QPlayers;
import com.demonwav.ectotoken.querydsl.QTransactions;
import com.demonwav.ectotoken.util.Util;

import com.mysema.query.QueryException;
import com.mysema.query.Tuple;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TokensManager {

    @Getter
    private static final TokensManager instance = new TokensManager();

    private TokensManager() {
    }

    // Get Balance
    public long getBalance(OfflinePlayer player) {
        return getBalance(player.getUniqueId());
    }

    public long getBalance(UUID uuid) {
        return getBalance(DatabaseManager.getInstance().getPlayerId(uuid));
    }

    public long getBalance(String name) {
        return getBalance(DatabaseManager.getInstance().getPlayerId(name));
    }

    public long getBalance(int i) {
        if (i < 0)
            return 0;

        SQLQuery query = DatabaseManager.getInstance().getNewQuery();
        QBalance b = QBalance.balance;
        Long result = query.from(b).where(b.id.eq(i)).uniqueResult(b.amount);
        return result == null ? 0 : result;
    }

    // Modify Balance
    public void modifyBalance(OfflinePlayer player, long amount, String description) {
        modifyBalance(player.getUniqueId(), amount, description);
    }

    public void modifyBalance(UUID uuid, long amount, String description) {
        modifyBalance(DatabaseManager.getInstance().getPlayerId(uuid), amount, description);
    }

    public void modifyBalance(String name, long amount, String description) {
        modifyBalance(DatabaseManager.getInstance().getPlayerId(name), amount, description);
    }

    public void modifyBalance(int i, long amount, String description) {
        if (i < 0)
            return;

        QBalance b = QBalance.balance;
        QTransactions t = QTransactions.transactions;

        try {
            SQLInsertClause balanceClause = DatabaseManager.getInstance().getInsertClause(b);
            SQLInsertClause transactionClause = DatabaseManager.getInstance().getInsertClause(t);

            transactionClause.columns(t.id, t.description, t.amount).values(i, description, amount).execute();
            balanceClause.columns(b.id, b.amount).values(i, amount).execute();
        } catch (QueryException e) {
            // Transaction insert won't fail, only balance will. when it does, update instead
            SQLUpdateClause clause = DatabaseManager.getInstance().getUpdateClause(b);

            clause.where(b.id.eq(i)).set(b.amount, b.amount.add(amount)).execute();
        }
    }

    // Set Balance
    public void setBalance(OfflinePlayer player, long amount, String description) {
        setBalance(player.getUniqueId(), amount, description);
    }

    public void setBalance(UUID uuid, long amount, String description) {
        setBalance(DatabaseManager.getInstance().getPlayerId(uuid), amount, description);
    }

    public void setBalance(String name, long amount, String description) {
        setBalance(DatabaseManager.getInstance().getPlayerId(name), amount, description);
    }

    public void setBalance(int i, long amount, String description) {
        if (i < 0)
            return;

        QBalance b = QBalance.balance;
        QTransactions t = QTransactions.transactions;

        // We need to know the current amount to get the amount changed for the transaction log
        SQLQuery query = DatabaseManager.getInstance().getNewQuery();
        Long current = query.from(b).where(b.id.eq(i)).uniqueResult(b.amount);
        current = current == null ? 0 : current;

        try {
            SQLInsertClause balanceClause = DatabaseManager.getInstance().getInsertClause(b);
            SQLInsertClause transactionClause = DatabaseManager.getInstance().getInsertClause(t);

            transactionClause.columns(t.id, t.description, t.amount).values(i, description, amount - current).execute();
            balanceClause.columns(b.id, b.amount).values(i, amount).execute();
        } catch (QueryException e) {
            // Transaction insert won't fail, only balance will. when it does, update instead
            SQLUpdateClause clause = DatabaseManager.getInstance().getUpdateClause(b);

            clause.where(b.id.eq(i)).set(b.amount, amount).execute();
        }
    }

    // Get all transactions
    public List<Transaction> getTransactions() {
        return getTransactions(Long.MAX_VALUE);
    }

    public List<Transaction> getTransactions(long limit) {
        QTransactions t = QTransactions.transactions;
        QPlayers p = QPlayers.players;

        SQLQuery query = DatabaseManager.getInstance().getNewQuery();

        List<Tuple> list = query
            .from(t)
            .join(p)
            .on(t.id.eq(p.id))
            .orderBy(t.datetime.asc())
            .limit(limit)
            .list(p.uuid, t.datetime, t.description, t.amount);

        return transformTuple(list);
    }

    // Get all transactions after/before a certain time
    public List<Transaction> getTransactions(Timestamp when, Transaction.Time time) {
        QTransactions t = QTransactions.transactions;
        QPlayers p = QPlayers.players;

        SQLQuery query = DatabaseManager.getInstance().getNewQuery();

        List<Tuple> list;

        if (time == Transaction.Time.BEFORE) {
            list = query
                .from(t)
                .join(p)
                .on(p.id.eq(t.id))
                .orderBy(t.datetime.asc())
                .where(t.datetime.lt(when))
                .list(p.uuid, t.datetime, t.description, t.amount);
        } else {
            list = query
                .from(t)
                .join(p)
                .on(p.id.eq(t.id))
                .orderBy(t.datetime.asc())
                .where(t.datetime.gt(when))
                .list(p.uuid, t.datetime, t.description, t.amount);
        }

        return transformTuple(list);
    }

    // Get all transactions between two times
    public List<Transaction> getTransactions(Timestamp before, Timestamp after) {
        QTransactions t = QTransactions.transactions;
        QPlayers p = QPlayers.players;

        SQLQuery query = DatabaseManager.getInstance().getNewQuery();

        List<Tuple> list = query
                .from(t)
                .join(p)
                .on(p.id.eq(t.id))
                .orderBy(t.datetime.asc())
                .where(t.datetime.between(before, after))
                .list(p.uuid, t.datetime, t.description, t.amount);

        return transformTuple(list);
    }

    // Get all transactions for a player less than/greater than an amount
    public List<Transaction> getTransactions(long change, Transaction.Amount amount) {
        QTransactions t = QTransactions.transactions;
        QPlayers p = QPlayers.players;

        SQLQuery query = DatabaseManager.getInstance().getNewQuery();

        List<Tuple> list;

        if (amount == Transaction.Amount.LESS) {
            list = query
                .from(t)
                .join(p)
                .on(p.id.eq(t.id))
                .orderBy(t.datetime.asc())
                .where(t.amount.lt(change))
                .list(p.uuid, t.datetime, t.description, t.amount);
        } else {
            list = query
                .from(t)
                .join(p)
                .on(p.id.eq(t.id))
                .orderBy(t.datetime.asc())
                .where(t.amount.gt(change))
                .list(p.uuid, t.datetime, t.description, t.amount);
        }
        return transformTuple(list);
    }

    // Get all transactions between two amounts
    public List<Transaction> getTransaction(long lowEnd, long highEnd) {
        QTransactions t = QTransactions.transactions;
        QPlayers p = QPlayers.players;

        SQLQuery query = DatabaseManager.getInstance().getNewQuery();

        List<Tuple> list = query
            .from(t)
            .join(p)
            .on(p.id.eq(t.id))
            .orderBy(t.datetime.asc())
            .where(t.amount.between(lowEnd, highEnd))
            .orderBy(t.datetime.asc())
            .list(p.uuid, t.datetime, t.description, t.amount);

        return transformTuple(list);
    }

    // Get all transactions for a player
    public List<Transaction> getTransactions(OfflinePlayer player) {
        return getTransactions(player.getUniqueId(), new Timestamp(new Date().getTime()), Transaction.Time.BEFORE);
    }

    public List<Transaction> getTransactions(UUID uuid) {
        return getTransactions(DatabaseManager.getInstance().getPlayerId(uuid), new Timestamp(new Date().getTime()), Transaction.Time.BEFORE);
    }

    public List<Transaction> getTransactions(String name) {
        return getTransactions(DatabaseManager.getInstance().getPlayerId(name), new Timestamp(new Date().getTime()), Transaction.Time.BEFORE);
    }

    public List<Transaction> getTransactions(int i) {
        return getTransactions(i, new Timestamp(new Date().getTime()), Transaction.Time.BEFORE);
    }

    // get all transactions for a player after/before a certain time
    public List<Transaction> getTransactions(OfflinePlayer player, Timestamp when, Transaction.Time time) {
        return getTransactions(player.getUniqueId(), when, time);
    }

    public List<Transaction> getTransactions(UUID uuid, Timestamp when, Transaction.Time time) {
        return getTransactions(DatabaseManager.getInstance().getPlayerId(uuid), when, time);
    }

    public List<Transaction> getTransactions(String name, Timestamp when, Transaction.Time time) {
        return getTransactions(DatabaseManager.getInstance().getPlayerId(name), when, time);
    }

    public List<Transaction> getTransactions(int i, Timestamp when, Transaction.Time time) {
        if (i < 0)
            return Collections.emptyList();

        QTransactions t = QTransactions.transactions;
        QPlayers p = QPlayers.players;

        SQLQuery query = DatabaseManager.getInstance().getNewQuery();

        List<Tuple> list;

        if (time == Transaction.Time.BEFORE) {
            list = query
                .from(t)
                .where(t.id.eq(i), t.datetime.lt(when))
                .orderBy(t.datetime.asc())
                .list(t.datetime, t.description, t.amount);
        } else {
            list = query
                .from(t)
                .where(t.id.eq(i), t.datetime.gt(when))
                .orderBy(t.datetime.asc())
                .list(t.datetime, t.description, t.amount);
        }

        query = DatabaseManager.getInstance().getNewQuery();

        UUID uuid = Util.byteToUUID(query.where(p.id.eq(i)).uniqueResult(p.uuid));
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        return transformTuple(list, player);
    }

    // Get all transactions for a player between two times
    public List<Transaction> getTransactions(OfflinePlayer player, Timestamp before, Timestamp after) {
        return getTransactions(player.getUniqueId(), before, after);
    }

    public List<Transaction> getTransactions(UUID uuid, Timestamp before, Timestamp after) {
        return getTransactions(DatabaseManager.getInstance().getPlayerId(uuid), before, after);
    }

    public List<Transaction> getTransactions(String name, Timestamp before, Timestamp after) {
        return getTransactions(DatabaseManager.getInstance().getPlayerId(name), before, after);
    }

    public List<Transaction> getTransactions(int i, Timestamp before, Timestamp after) {
        if (i < 0)
            return Collections.emptyList();

        QTransactions t = QTransactions.transactions;
        QPlayers p = QPlayers.players;

        SQLQuery query = DatabaseManager.getInstance().getNewQuery();

        List<Tuple> list = query
                .from(t)
                .where(t.id.eq(i), t.datetime.between(before, after))
                .orderBy(t.datetime.asc())
                .list(t.datetime, t.description, t.amount);

        query = DatabaseManager.getInstance().getNewQuery();

        UUID uuid = Util.byteToUUID(query.where(p.id.eq(i)).uniqueResult(p.uuid));
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        return transformTuple(list, player);
    }

    // Get all transactions for a player less than/greater than an amount
    public List<Transaction> getTransactions(OfflinePlayer player, long change, Transaction.Amount amount) {
        return getTransactions(player.getUniqueId(), change, amount);
    }

    public List<Transaction> getTransactions(UUID uuid, long change, Transaction.Amount amount) {
        return getTransactions(DatabaseManager.getInstance().getPlayerId(uuid), change, amount);
    }

    public List<Transaction> getTransactions(String name, long change, Transaction.Amount amount) {
        return getTransactions(DatabaseManager.getInstance().getPlayerId(name), change, amount);
    }

    public List<Transaction> getTransactions(int i, long change, Transaction.Amount amount) {
        if (i < 0)
            return Collections.emptyList();

        QTransactions t = QTransactions.transactions;
        QPlayers p = QPlayers.players;

        SQLQuery query = DatabaseManager.getInstance().getNewQuery();

        List<Tuple> list;

        if (amount == Transaction.Amount.LESS) {
            list = query
                .from(t)
                .where(t.id.eq(i), t.amount.lt(change))
                .orderBy(t.datetime.asc())
                .list(t.datetime, t.description, t.amount);
        } else {
            list = query
                .from(t)
                .where(t.id.eq(i), t.amount.gt(change))
                .orderBy(t.datetime.asc())
                .list(t.datetime, t.description, t.amount);
        }

        query = DatabaseManager.getInstance().getNewQuery();

        UUID uuid = Util.byteToUUID(query.where(p.id.eq(i)).uniqueResult(p.uuid));
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        return transformTuple(list, player);
    }

    // Get all transactions for a player between two amounts
    public List<Transaction> getTransactions(OfflinePlayer player, long lowEnd, long highEnd) {
        return getTransactions(player.getUniqueId(), lowEnd, highEnd);
    }

    public List<Transaction> getTransactions(UUID uuid, long lowEnd, long highEnd) {
        return getTransactions(DatabaseManager.getInstance().getPlayerId(uuid), lowEnd, highEnd);
    }

    public List<Transaction> getTransactions(String name, long lowEnd, long highEnd) {
        return getTransactions(DatabaseManager.getInstance().getPlayerId(name), lowEnd, highEnd);
    }

    public List<Transaction> getTransactions(int i, long lowEnd, long highEnd) {
        if (i < 0)
            return Collections.emptyList();

        QTransactions t = QTransactions.transactions;
        QPlayers p = QPlayers.players;

        SQLQuery query = DatabaseManager.getInstance().getNewQuery();

        List<Tuple> list = query
            .from(t)
            .where(t.id.eq(i), t.amount.between(lowEnd, highEnd))
            .orderBy(t.datetime.asc())
            .list(t.datetime, t.description, t.amount);

        query = DatabaseManager.getInstance().getNewQuery();

        UUID uuid = Util.byteToUUID(query.where(p.id.eq(i)).uniqueResult(p.uuid));
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        return transformTuple(list, player);
    }

    private List<Transaction> transformTuple(List<Tuple> tuples) {
        QTransactions t = QTransactions.transactions;
        QPlayers p = QPlayers.players;

        List<Transaction> transactions = new ArrayList<>(tuples.size());
        tuples.stream().forEach(tuple -> {
            UUID uuid = Util.byteToUUID(tuple.get(p.uuid));
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            Timestamp datetime = tuple.get(t.datetime);
            String description = tuple.get(t.description);
            Long amount = tuple.get(t.amount);
            transactions.add(new Transaction(player, datetime, description, amount == null ? 0 : amount));
        });
        return transactions;
    }

    private List<Transaction> transformTuple(List<Tuple> tuples, OfflinePlayer player) {
        QTransactions t = QTransactions.transactions;
        List<Transaction> transactions = new ArrayList<>(tuples.size());
        tuples.stream().forEach(tuple -> {
            Timestamp datetime = tuple.get(t.datetime);
            String description = tuple.get(t.description);
            Long amount = tuple.get(t.amount);
            transactions.add(new Transaction(player, datetime, description, amount == null ? 0 : amount));
        });
        return transactions;
    }
}
