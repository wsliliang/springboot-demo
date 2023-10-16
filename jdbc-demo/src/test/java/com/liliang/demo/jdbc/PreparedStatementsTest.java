package com.liliang.demo.jdbc;

import org.junit.Before;
import org.junit.Test;

import java.sql.*;

public class PreparedStatementsTest {
    public static final String USER_NAME = "root";
    public static final String PASSWORD = "123456";
    public static final String SQL_TRUNCATE = "truncate table user";

    @Before
    public void setup() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConn(boolean useServerPrepStmts) throws SQLException {
        final String url = "jdbc:mysql://localhost:3306/test?rewriteBatchedStatements=true&useServerPrepStmts=" + useServerPrepStmts;
        return DriverManager.getConnection(url, USER_NAME, PASSWORD);
    }

    /**
     * 测试prepStmts为true
     *
     * @throws SQLException sqlException
     */
    @Test
    public void testPrepStmtsTrue() throws SQLException {
        Connection conn = getConn(true);
        PreparedStatement statement = conn.prepareStatement("insert into user(name) values(?)");
        statement.setString(1, "test");
        statement.executeUpdate();
    }

    /**
     * 测试prepStmts为true
     * 2个参数，可以观察数据包
     *
     * @throws SQLException sqlException
     */
    @Test
    public void testPrepStmtsTrue2() throws SQLException {
        Connection conn = getConn(true);
        PreparedStatement statement = conn.prepareStatement("insert into user(id, name) values(?,?)");
        statement.setInt(1, 556);
        statement.setString(2, "test");
        statement.executeUpdate();
    }

    /**
     * 测试prepStmts为true
     * 多次执行，看看prepareStatement的作用域
     *
     * @throws SQLException sqlException
     */
    @Test
    public void testPrepStmtsTrue3() throws SQLException {
        Connection conn = getConn(true);
        PreparedStatement statement = conn.prepareStatement("insert into user(name) values(?)");
        statement.setString(1, "test");
        statement.executeUpdate();

        statement.setString(1, "test2");
        statement.executeUpdate();

        statement.close();
        statement = conn.prepareStatement("insert into user(name) values(?)");
        statement.setString(1, "test3");
        statement.executeUpdate();
    }

    /**
     * 测试prepStmts为false
     *
     * @throws SQLException sqlException
     */
    @Test
    public void testPrepStmtsFalse() throws SQLException {
        Connection conn = getConn(false);
        PreparedStatement statement = conn.prepareStatement("insert into user(id, name) values(?,?)");
        statement.setInt(1, 557);
        statement.setString(2, "test");
        statement.executeUpdate();
    }

    /**
     * 测试SQL注入
     *
     * @throws SQLException sqlException
     */
    @Test
    public void testSqlInject() throws SQLException {
        Connection conn = getConn(false);
        String name = "'test' or 1=1;";
        PreparedStatement statement = conn.prepareStatement("select id,name from user where name = " + name);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name1 = resultSet.getString("name");
            System.out.println("id:" + id + ",name:" + name1);
        }
    }


    /**
     * 测试解决SQL注入
     *
     * @throws SQLException sqlException
     */
    @Test
    public void testPrepStmtsQuery() throws SQLException {
        Connection conn = getConn(false);
        String name = "'test' or 1=1";
        PreparedStatement statement = conn.prepareStatement("select id,name from user where name = ?");
        statement.setString(1, name);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name1 = resultSet.getString("name");
            System.out.println("id:" + id + ",name:" + name1);
        }
    }

    /**
     * 测试statement
     *
     * @throws SQLException sqlException
     */
    @Test
    public void testStmtsQuery() throws SQLException {
        Connection conn = getConn(false);
        String name = "'test' or 1=1";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("select id,name from user where name = " + name);
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name1 = resultSet.getString("name");
            System.out.println("id:" + id + ",name:" + name1);
        }
    }

    /**
     * 测试prepStmts为true
     * 测试结果：与写sql一样
     * 03 69 6e 73 65 72 74 20     . i n s e r t
     * 69 6e 74 6f 20 75 73 65     i n t o   u s e
     * 72 28 69 64 2c 20 6e 61     r ( i d ,   n a
     * 6d 65 29 20 76 61 6c 75     m e )   v a l u
     * 65 73 28 31 2c 27 74 65     e s ( 1 , ' t e
     * 73 74 31 27 29 2c 28 32     s t 1 ' ) , ( 2
     * 2c 27 74 65 73 74 32 27     , ' t e s t 2 '
     * 29                          )
     *
     * @throws SQLException sqlException
     */
    @Test
    public void testBatchStatement() throws SQLException {
        Connection conn = getConn(false);
        PreparedStatement statement = conn.prepareStatement("insert into user(id, name) values(?,?)");
        statement.setInt(1, 1);
        statement.setString(2, "test1");
        statement.addBatch();
        statement.setInt(1, 2);
        statement.setString(2, "test2");
        statement.addBatch();
        statement.executeBatch();
    }

    /**
     * 测试主键生成-单条插入
     *
     * @throws SQLException sqlException
     */
    @Test
    public void testGenerateKey1() throws SQLException {
        Connection conn = getConn(false);
        conn.setAutoCommit(false);
        PreparedStatement stmt = conn.prepareStatement(SQL_TRUNCATE);
        stmt.executeUpdate();
        conn.commit();
        stmt.close();

        String sql = "insert into user(id, name) values(?,?)";
        PreparedStatement stmt2 = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
        stmt2.setNull(1, Types.INTEGER);
        stmt2.setString(2, "name1");
        stmt2.executeUpdate();
        ResultSet rs = stmt2.getGeneratedKeys();

        // 1
        System.out.print("ids generated: ");
        while (rs.next()) {
            Object value = rs.getObject(1);
            System.out.print(value + " ");
        }
    }


    /**
     * 测试主键生成
     *
     * @throws SQLException sqlException
     */
    @Test
    public void testGenerateKey() throws SQLException {
        Connection conn = getConn(false);
        conn.setAutoCommit(false);
        PreparedStatement stmt = conn.prepareStatement(SQL_TRUNCATE);
        stmt.executeUpdate();
        conn.commit();
        stmt.close();

        String sql = "insert into user(id, name) values(?,?)";
        PreparedStatement stmt2 = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
        // 1,2,3,4,5
        for (int i = 1; i <= 5; i++) {
            stmt2.setInt(1, i);
            stmt2.setString(2, "name" + i);
            stmt2.addBatch();
        }
        stmt2.executeBatch();
        ResultSet rs = stmt2.getGeneratedKeys();

        // 5,6,7,8,9
        System.out.print("ids generated: ");
        while (rs.next()) {
            Object value = rs.getObject(1);
            System.out.print(value + " ");
        }
    }

    /**
     * 测试主键生成-批量插入
     *
     * @throws SQLException sqlException
     */
    @Test
    public void testGenerateKey3() throws SQLException {
        Connection conn = getConn(false);
        conn.setAutoCommit(false);
        PreparedStatement stmt = conn.prepareStatement(SQL_TRUNCATE);
        stmt.executeUpdate();
        conn.commit();
        stmt.close();

        String sql = "insert into user(id, name) values(?,?)";
        PreparedStatement stmt2 = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
        // 1,null,3,null,5
        for (int i = 1; i <= 5; i++) {
            if (i == 1 || i == 3 || i == 5) {
                stmt2.setInt(1, i);
            } else {
                stmt2.setNull(1, Types.INTEGER);
            }
            stmt2.setString(2, "name" + i);
            stmt2.addBatch();
        }
        stmt2.executeBatch();
        ResultSet rs = stmt2.getGeneratedKeys();

        // 2,3,4,5,6
        System.out.print("ids generated: ");
        while (rs.next()) {
            Object value = rs.getObject(1);
            System.out.print(value + " ");
        }
    }

}
