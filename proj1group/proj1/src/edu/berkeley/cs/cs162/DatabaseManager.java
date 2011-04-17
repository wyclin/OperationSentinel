package edu.berkeley.cs.cs162;

import com.mchange.v2.c3p0.*;
import java.beans.PropertyVetoException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.TreeSet;

public class DatabaseManager {

    public static final String databaseHost = "ec2-50-17-180-71.compute-1.amazonaws.com";
    public static final String databaseUser = "group21";
    public static final String databasePassword = "zjKkzjSs";
    public static final String database = "group21";
    public static final DateFormat timestampFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private ComboPooledDataSource dataSource;

    public DatabaseManager() {
        super();
        this.dataSource = new ComboPooledDataSource();
        try {
            this.dataSource.setDriverClass("com.mysql.jdbc.Driver");
            this.dataSource.setJdbcUrl("jdbc:mysql://" + databaseHost + ":3306/" + database);
            this.dataSource.setUser(databaseUser);
            this.dataSource.setPassword(databasePassword);
        } catch (PropertyVetoException e) {
        }
    }

    public void emptyDatabase() throws SQLException {
        String query1 = "DELETE FROM `InGroup`;";
        String query2 = "DELETE FROM `MessageReceivers`";
        String query3 = "DELETE FROM `OfflineMessages`";
        Connection connection = null;
        Statement statement1 = null;
        Statement statement2 = null;
        Statement statement3 = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            statement1 = connection.createStatement();
            statement2 = connection.createStatement();
            statement3 = connection.createStatement();
            statement1.executeUpdate(query1);
            statement2.executeUpdate(query2);
            statement3.executeUpdate(query3);
            connection.commit();
        } finally {
            if (statement1 != null) {statement1.close();}
            if (statement2 != null) {statement2.close();}
            if (statement3 != null) {statement3.close();}
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    public HashMap<String, Object> getReceiver(String name) throws SQLException {
        String query = "SELECT `name`,`password`,`type` FROM `MessageReceivers` WHERE `name`='" + name +"';";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            ResultSet results = statement.executeQuery(query);
            HashMap<String, Object> receiverProperties = null;
            if (results.next()) {
                receiverProperties = new HashMap<String, Object>();
                receiverProperties.put("name", results.getString(1));
                receiverProperties.put("password", results.getString(2));
                receiverProperties.put("type", results.getString(3));
            }
            return receiverProperties;
        } finally {
            if (statement != null) {statement.close();}
            if (connection != null) {connection.close();}
        }
    }

    public HashMap<String, Object> getUser(String userName) throws SQLException {
        String query = "SELECT `name`,`password` FROM `MessageReceivers` WHERE `name`='" + userName +"' AND `type`='user';";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            ResultSet results = statement.executeQuery(query);
            HashMap<String, Object> userProperties = null;
            if (results.next()) {
                userProperties = new HashMap<String, Object>();
                userProperties.put("name", results.getString(1));
                userProperties.put("password", results.getString(2));
            }
            return userProperties;
        } finally {
            if (statement != null) {statement.close();}
            if (connection != null) {connection.close();}
        }
    }

    public int getUserCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM `MessageReceivers` WHERE `type`='user';";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            ResultSet results = statement.executeQuery(query);
            results.next();
            return results.getInt(1);
        } finally {
            if (statement != null) {statement.close();}
            if (connection != null) {connection.close();}
        }
    }

    public TreeSet<String> getUserList() throws SQLException {
        String query = "SELECT `name` FROM `MessageReceivers` WHERE `type`='user';";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            ResultSet results = statement.executeQuery(query);
            TreeSet<String> userList = new TreeSet<String>();
            while (results.next()) {
                userList.add(results.getString(1));
            }
            return userList;
        } finally {
            if (statement != null) {statement.close();}
            if (connection != null) {connection.close();}
        }
    }

    public HashMap<String, Object> getGroup(String groupName) throws SQLException {
        String query = "SELECT `name` FROM `MessageReceivers` WHERE `name`='" + groupName +"' AND `type`='group';";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            ResultSet results = statement.executeQuery(query);
            HashMap<String, Object> groupProperties = null;
            if (results.next()) {
                groupProperties = new HashMap<String, Object>();
                groupProperties.put("name", results.getString(1));
            }
            return groupProperties;
        } finally {
            if (statement != null) {statement.close();}
            if (connection != null) {connection.close();}
        }
    }

    public int getGroupCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM `MessageReceivers` WHERE `type`='group';";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            ResultSet results = statement.executeQuery(query);
            results.next();
            return results.getInt(1);
        } finally {
            if (statement != null) {statement.close();}
            if (connection != null) {connection.close();}
        }
    }

    public TreeSet<String> getGroupList() throws SQLException {
        String query = "SELECT `name` FROM `MessageReceivers` WHERE `type`='group';";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            ResultSet results = statement.executeQuery(query);
            TreeSet<String> groupList = new TreeSet<String>();
            while (results.next()) {
                groupList.add(results.getString(1));
            }
            return groupList;
        } finally {
            if (statement != null) {statement.close();}
            if (connection != null) {connection.close();}
        }
    }

    public int getGroupUserCount(String groupName) throws SQLException {
        String query = "SELECT COUNT(*) FROM `InGroup` WHERE `group_id`=(SELECT `receiver_id` FROM `MessageReceivers` WHERE `name`='" + groupName + "');";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            ResultSet results = statement.executeQuery(query);
            results.next();
            return results.getInt(1);
        } finally {
            if (statement != null) {statement.close();}
            if (connection != null) {connection.close();}
        }
    }

    public TreeSet<String> getGroupUserList(String groupName) throws SQLException {
        String query = "SELECT t2.`name` FROM `InGroup` AS t1 INNER JOIN `MessageReceivers` AS t2 ON t1.`user_id`=t2.`receiver_id` WHERE `group_id`=(SELECT `receiver_id` FROM `MessageReceivers` WHERE `name`='" + groupName + "' AND `type`='group') AND `type`='user';";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            ResultSet results = statement.executeQuery(query);
            TreeSet<String> groupUserList = new TreeSet<String>();
            while (results.next()) {
                groupUserList.add(results.getString(1));
            }
            return groupUserList;
        } finally {
            if (statement != null) {statement.close();}
            if (connection != null) {connection.close();}
        }
    }

    public void addUser(String userName, String password) throws SQLException {
        String query = "INSERT INTO `MessageReceivers` (`name`,`password`,`type`) VALUES ('" + userName + "','" + password + "','user')";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } finally {
            if (statement != null) {statement.close();}
            if (connection != null) {connection.close();}
        }
    }

    public void removeUser(String userName) throws SQLException {
        String query = "DELETE FROM `MessageReceivers` WHERE `NAME`='" + userName + "' AND `type`='user';";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } finally {
            if (statement != null) {statement.close();}
            if (connection != null) {connection.close();}
        }
    }

    public void addUserToGroup(String userName, String groupName) throws SQLException {
        String query = "INSERT INTO `InGroup` (`group_id`,`user_id`) VALUES ((SELECT `receiver_id` FROM `MessageReceivers` WHERE `name`='" + groupName + "' AND `type`='group'), (SELECT `receiver_id` FROM `MessageReceivers` WHERE `name`='" + userName + "' AND `type`='user'));";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } finally {
            if (statement != null) {statement.close();}
            if (connection != null) {connection.close();}
        }
    }

    public void removeUserFromGroup(String userName, String groupName) throws SQLException {
        String query = "DELETE FROM `InGroup` WHERE `group_id`=(SELECT `receiver_id` FROM `MessageReceivers` WHERE `name`='" + groupName + "' AND `type`='group') AND `user_id`=(SELECT `receiver_id` FROM `MessageReceivers` WHERE `name`='" + userName + "' AND `type`='user')";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } finally {
            if (statement != null) {statement.close();}
            if (connection != null) {connection.close();}
        }
    }

    public void addGroup(String groupName) throws SQLException {
        String query = "INSERT INTO `MessageReceivers` (`name`,`type`) VALUES ('" + groupName + "','group')";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } finally {
            if (statement != null) {statement.close();}
            if (connection != null) {connection.close();}
        }
    }

    public void removeGroup(String groupName) throws SQLException {
        String query = "DELETE FROM `MessageReceivers` WHERE `NAME`='" + groupName + "' AND `type`='group';";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } finally {
            if (statement != null) {statement.close();}
            if (connection != null) {connection.close();}
        }
    }

    public void logMessage(String userName, Message message) throws SQLException {
        String query = "INSERT INTO `OfflineMessages` (`user_id`,`timestamp`,`sqn`,`sender`,`receiver`,`text`) VALUES ((SELECT `receiver_id` FROM `MessageReceivers` WHERE `name`='" + userName + "' AND `type`='user'),'" + timestampFormat.format(message.date) + "','" + Integer.toString(message.sqn) + "','" + message.sender.getUserName() + "','" + message.receiver + "','" + message.text + "');";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } finally {
            if (statement != null) {statement.close();}
            if (connection != null) {connection.close();}
        }
    }

        public LinkedList<HashMap<String, Object>> getOfflineMessages(String userName) throws SQLException {
        String query1 = "SELECT `timestamp`,`sqn`,`sender`,`receiver`,`text` FROM `OfflineMessages` WHERE `user_id`=(SELECT `receiver_id` FROM `MessageReceivers` WHERE `name`='" + userName + "') ORDER BY `timestamp` ASC;";
        String query2 = "DELETE FROM `OfflineMessages` WHERE `user_id`=(SELECT `receiver_id` FROM `MessageReceivers` WHERE `name`='" + userName + "');";
        Connection connection = null;
        Statement statement1 = null;
        Statement statement2 = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            statement1 = connection.createStatement();
            statement2 = connection.createStatement();
            ResultSet results = statement1.executeQuery(query1);
            statement2.executeUpdate(query2);
            connection.commit();
            LinkedList<HashMap<String, Object>> messages = new LinkedList<HashMap<String, Object>>();
            HashMap<String, Object> messageProperties;
            while (results.next()) {
                messageProperties = new HashMap<String, Object>();
                messageProperties.put("timestamp", results.getDate(1));
                messageProperties.put("sqn", results.getInt(2));
                messageProperties.put("sender", results.getString(3));
                messageProperties.put("receiver", results.getString(4));
                messageProperties.put("text", results.getString(5));
                messages.add(messageProperties);
            }
            return messages;
        } finally {
            if (statement1 != null) {statement1.close();}
            if (statement2 != null) {statement2.close();}
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }
}
