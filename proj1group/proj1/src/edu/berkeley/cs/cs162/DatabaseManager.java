package edu.berkeley.cs.cs162;

import java.sql.*;
import java.util.Properties;
import java.util.TreeSet;

public class DatabaseManager {

    public static final String databaseHost = "ec2-50-17-180-71.compute-1.amazonaws.com";
    public static final String databaseUser = "group21";
    public static final String databasePassword = "zjKkzjSs";
    public static final String database = "group21";

    private Connection connect() throws SQLException {
        Properties connectionProperties = new Properties();
        connectionProperties.setProperty("user", databaseUser);
        connectionProperties.setProperty("password", databasePassword);
        return DriverManager.getConnection("jdbc:mysql://" + databaseHost + "/" + database, connectionProperties);
    }

    public void emptyDatabase() throws SQLException {
        String query1 = "DELETE FROM `InGroup`;";
        String query2 = "DELETE FROM `MessageReceivers`";
        String query3 = "DELETE FROM `OfflineMessages`";
        Statement statement1 = null;
        Statement statement2 = null;
        Statement statement3 = null;
        try {
            Connection connection = connect();
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
            connect().setAutoCommit(true);
        }
    }

    public Properties getUser(String userName) throws SQLException {
        String query = "SELECT `name`,`password` FROM `MessageReceivers` WHERE `name`='" + userName +"' AND `type`='user';";
        Statement statement = null;
        try {
            statement = connect().createStatement();
            ResultSet results = statement.executeQuery(query);
            Properties userProperties = null;
            if (results.next()) {
                userProperties = new Properties();
                userProperties.setProperty("name", results.getString(1));
                userProperties.setProperty("password", results.getString(2));
            }
            return userProperties;
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public int getUserCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM `MessageReceivers` WHERE `type`='user';";
        Statement statement = null;
        try {
            statement = connect().createStatement();
            ResultSet results = statement.executeQuery(query);
            results.next();
            return results.getInt(1);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public TreeSet<String> getUserList() throws SQLException {
        String query = "SELECT `name` FROM `MessageReceivers` WHERE `type`='user';";
        Statement statement = null;
        try {
            statement = connect().createStatement();
            ResultSet results = statement.executeQuery(query);
            TreeSet<String> userList = new TreeSet<String>();
            while (results.next()) {
                userList.add(results.getString(1));
            }
            return userList;
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public Properties getGroup(String groupName) throws SQLException {
        String query = "SELECT `name` FROM `MessageReceivers` WHERE `name`='" + groupName +"' AND `type`='group';";
        Statement statement = null;
        try {
            statement = connect().createStatement();
            ResultSet results = statement.executeQuery(query);
            Properties groupProperties = null;
            if (results.next()) {
                groupProperties = new Properties();
                groupProperties.setProperty("name", results.getString(1));
            }
            return groupProperties;
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public int getGroupCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM `MessageReceivers` WHERE `type`='group';";
        Statement statement = null;
        try {
            statement = connect().createStatement();
            ResultSet results = statement.executeQuery(query);
            results.next();
            return results.getInt(1);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public TreeSet<String> getGroupList() throws SQLException {
        String query = "SELECT `name` FROM `MessageReceivers` WHERE `type`='group';";
        Statement statement = null;
        try {
            statement = connect().createStatement();
            ResultSet results = statement.executeQuery(query);
            TreeSet<String> groupList = new TreeSet<String>();
            while (results.next()) {
                groupList.add(results.getString(1));
            }
            return groupList;
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public int getGroupUserCount(String groupName) throws SQLException {
        String query = "SELECT COUNT(*) FROM `InGroup` WHERE `group_id`=(SELECT `receiver_id` FROM `MessageReceivers` WHERE `name`='" + groupName + "');";
        Statement statement = null;
        try {
            statement = connect().createStatement();
            ResultSet results = statement.executeQuery(query);
            results.next();
            return results.getInt(1);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public TreeSet<String> getGroupUserList(String groupName) throws SQLException {
        String query = "SELECT t2.`name` FROM `InGroup` AS t1 INNER JOIN `MessageReceivers` AS t2 ON t1.`user_id`=t2.`receiver_id` WHERE `group_id`=(SELECT `receiver_id` FROM `MessageReceivers` WHERE `name`='" + groupName + "' AND `type`='group') AND `type`='user';";
        Statement statement = null;
        try {
            statement = connect().createStatement();
            ResultSet results = statement.executeQuery(query);
            TreeSet<String> groupUserList = new TreeSet<String>();
            while (results.next()) {
                groupUserList.add(results.getString(1));
            }
            return groupUserList;
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public void addUser(String userName, String password) throws SQLException {
        String query = "INSERT INTO `MessageReceivers` (`name`,`password`,`type`) VALUES ('" + userName + "','" + password + "','user')";
        Statement statement = null;
        try {
            statement = connect().createStatement();
            statement.executeUpdate(query);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public void removeUser(String userName) throws SQLException {
        String query = "DELETE FROM `MessageReceivers` WHERE `NAME`='" + userName + "' AND `type`='user';";
        Statement statement = null;
        try {
            statement = connect().createStatement();
            statement.executeUpdate(query);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public void addUserToGroup(String userName, String groupName) throws SQLException {
        String query = "INSERT INTO `InGroup` (`group_id`,`user_id`) VALUES ((SELECT `receiver_id` FROM `MessageReceivers` WHERE `name`='" + groupName + "' AND `type`='group'), (SELECT `receiver_id` FROM `MessageReceivers` WHERE `name`='" + userName + "' AND `type`='user'));";
        Statement statement = null;
        try {
            statement = connect().createStatement();
            statement.executeUpdate(query);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public void removeUserFromGroup(String userName, String groupName) throws SQLException {
        String query = "DELETE FROM `InGroup` WHERE `group_id`=(SELECT `receiver_id` FROM `MessageReceivers` WHERE `name`='" + groupName + "' AND `type`='group') AND `user_id`=(SELECT `receiver_id` FROM `MessageReceivers` WHERE `name`='" + userName + "' AND `type`='user')";
        Statement statement = null;
        try {
            statement = connect().createStatement();
            statement.executeUpdate(query);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public void addGroup(String groupName) throws SQLException {
        String query = "INSERT INTO `MessageReceivers` (`name`,`type`) VALUES ('" + groupName + "','group')";
        Statement statement = null;
        try {
            statement = connect().createStatement();
            statement.executeUpdate(query);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public void removeGroup(String groupName) throws SQLException {
        String query = "DELETE FROM `MessageReceivers` WHERE `NAME`='" + groupName + "' AND `type`='group';";
        Statement statement = null;
        try {
            statement = connect().createStatement();
            statement.executeUpdate(query);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }
}
