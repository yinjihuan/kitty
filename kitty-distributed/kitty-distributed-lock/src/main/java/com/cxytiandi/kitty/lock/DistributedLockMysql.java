package com.cxytiandi.kitty.lock;


import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Mysql 分布式锁
 *
 * @作者 尹吉欢
 * @个人微信 jihuan900
 * @微信公众号 猿天地
 * @GitHub https://github.com/yinjihuan
 * @作者介绍 http://cxytiandi.com/about
 * @时间 2020-03-31 22:53
 */
@Slf4j
public class DistributedLockMysql implements DistributedLock {

    private DataSource dataSource;

    public DistributedLockMysql(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public <T> T lock(String key, int waitTime, int leaseTime, Supplier<T> success, Supplier<T> fail) {
        if (dataSource == null) {
            log.warn("未配置Mysql锁数据源");
            return fail.get();
        }

        Connection conn = null;
        Boolean connAutoCommit = null;
        PreparedStatement preparedStatement = null;

        try {
            conn = dataSource.getConnection();
            connAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            preparedStatement = conn.prepareStatement(  "insert into kitty_lock values(?)");
            preparedStatement.setString(1, key);
            preparedStatement.execute();
            conn.commit();
            return success.get();
        } catch (Exception e) {
           return fail.get();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(connAutoCommit);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            // close PreparedStatement
            if (null != preparedStatement) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                   throw new RuntimeException(e);
                }
            }
            deleteLock(key);
        }

    }

    @Override
    public <T> T lock(String key, int leaseTime, Supplier<T> success, Supplier<T> fail) {
        return lock(key, 0, leaseTime, success, fail);
    }

    @Override
    public <T> T lock(String key, int leaseTime, TimeUnit timeUnit, Supplier<T> success, Supplier<T> fail) {
        // todo: Mysql锁自动失效待实现
        return lock(key, 0, leaseTime, success, fail);
    }

    private void deleteLock(String key) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        try {
            conn = dataSource.getConnection();

            preparedStatement = conn.prepareStatement(  "delete from kitty_lock where lock_name = ?");
            preparedStatement.setString(1, key);
            preparedStatement.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // commit
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            // close PreparedStatement
            if (null != preparedStatement) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}