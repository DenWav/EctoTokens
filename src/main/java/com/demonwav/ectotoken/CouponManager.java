package com.demonwav.ectotoken;

import com.demonwav.ectotoken.querydsl.Coupon;
import com.demonwav.ectotoken.querydsl.QCoupon;
import com.demonwav.ectotoken.querydsl.QCouponUse;

import com.mysema.query.QueryException;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.dml.SQLDeleteClause;
import com.mysema.query.sql.dml.SQLInsertClause;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;

public class CouponManager {
    @Getter
    private static final CouponManager instance = new CouponManager();

    private CouponManager() {}

    public List<Coupon> getAllCoupons() {
        QCoupon c = QCoupon.coupon;
        SQLQuery query = DatabaseManager.getInstance().getNewQuery();

        return query.from(c).list(c);
    }

    public Coupon getCouponById(int id) {
        QCoupon c = QCoupon.coupon;
        SQLQuery query = DatabaseManager.getInstance().getNewQuery();

        return query.from(c).where(c.couponId.eq(id)).uniqueResult(c);
    }

    public Coupon getCouponByName(String name) {
        QCoupon c = QCoupon.coupon;
        SQLQuery query = DatabaseManager.getInstance().getNewQuery();

        return query.from(c).where(c.couponName.eq(name)).uniqueResult(c);
    }

    public Timestamp getWhenCouponUsed(int playerId, int couponId) {
        QCouponUse c = QCouponUse.couponUse;
        SQLQuery query = DatabaseManager.getInstance().getNewQuery();

        return query.from(c).where(c.playerId.eq(playerId), c.couponId.eq(couponId)).uniqueResult(c.datetime);
    }

    public Timestamp getWhenCouponUsed(int playerId, String couponName) {
        QCoupon c = QCoupon.coupon;
        QCouponUse u = QCouponUse.couponUse;
        SQLQuery query = DatabaseManager.getInstance().getNewQuery();

        return query.from(u).join(c).on(u.couponId.eq(c.couponId)).where(c.couponName.eq(couponName), u.playerId.eq(playerId)).uniqueResult(u.datetime);
    }

    public boolean createCoupon(String name, long amount, int uses) {
        QCoupon c = QCoupon.coupon;
        SQLInsertClause clause = DatabaseManager.getInstance().getInsertClause(c);

        try {
            clause.columns(c.couponName, c.amount, c.uses).values(name, amount, uses).execute();
            return true;
        } catch (QueryException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteCoupon(String name) {
        QCoupon c = QCoupon.coupon;
        SQLDeleteClause clause = DatabaseManager.getInstance().getDeleteCluase(c);

        clause.where(c.couponName.eq(name)).execute();
    }

    public void deleteCoupon(int couponId) {
        QCoupon c = QCoupon.coupon;
        SQLDeleteClause clause = DatabaseManager.getInstance().getDeleteCluase(c);

        clause.where(c.couponId.eq(couponId)).execute();
    }

    public boolean addCouponUse(int playerId, int couponId) {
        QCoupon c = QCoupon.coupon;
        QCouponUse u = QCouponUse.couponUse;

        SQLQuery query = DatabaseManager.getInstance().getNewQuery();

        Integer uses = query.from(c).where(c.couponId.eq(couponId)).uniqueResult(c.uses);
        uses = uses == null ? 0 : uses;

        // There is a defined number of uses for this coupon
        // So check how many times it's been used so far
        if (uses != 0) {
            query = DatabaseManager.getInstance().getNewQuery();
            long usages = query.from(u).where(u.couponId.eq(couponId)).count();
            if (usages >= uses) {
                // The coupon has been used up completely, return false
                return false;
            }
        }

        SQLInsertClause clause = DatabaseManager.getInstance().getInsertClause(u);

        try {
            clause.columns(u.playerId, u.couponId).values(playerId, couponId).execute();
            return true;
        } catch (QueryException e) {
            return false;
        }
    }

    public boolean addCouponUse(int playerId, String couponName) {
        QCoupon c = QCoupon.coupon;
        SQLQuery query = DatabaseManager.getInstance().getNewQuery();

        Integer id = query.from(c).where(c.couponName.eq(couponName)).uniqueResult(c.couponId);
        return id != null && addCouponUse(playerId, id);
    }
}
