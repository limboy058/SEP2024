package alex.UTFProject.util;

import java.sql.*;

import static alex.UTFProject.common.CommonConstants.DBPassword;

public class UpdateDatabaseUtil {

    public static void updateFood() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://124.71.207.55:3306/utf?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true","root",DBPassword);
        Statement statement = connection.createStatement();
        String sql = "SELECT * FROM utf.dish;";
        ResultSet resultSet = statement.executeQuery(sql);
        while(resultSet.next()){
            String content=resultSet.getString("name");
            tryInsertFoodAndFoodRelation(content);
            tryInsertFoodIntoFoodAndPersonRelation(content);
        }
        resultSet.close();
        statement.close();
        connection.close();
    }

    public static void tryInsertWordIntoKeyWord(String food) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://124.71.207.55:3306/utf?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true","root",DBPassword);
        Statement statement = connection.createStatement();
        String sql = "SELECT * FROM utf.user;";
        PreparedStatement resultStatement= connection.prepareStatement
                ("select * from utf.userfoodrelation where user = ? and food = ?");
        ResultSet resultSet = statement.executeQuery(sql);
        resultStatement.setString(2,food);
        while(resultSet.next()){
            String person=resultSet.getString("name");
            resultStatement.setString(1,person);
            ResultSet resultSet1=resultStatement.executeQuery();
            if(!resultSet1.next()){
                PreparedStatement ps=connection.prepareStatement
                        ("insert into utf.userfoodrelation (user, food) value (?,?)");
                ps.setString(1,person);
                ps.setString(2,food);
                ps.executeUpdate();
            }
        }
        resultSet.close();
        statement.close();
        resultStatement.close();
        connection.close();
    }
    public static void tryInsertFoodIntoFoodAndPersonRelation(String food) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://124.71.207.55:3306/utf?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true","root",DBPassword);
        Statement statement = connection.createStatement();
        String sql = "SELECT * FROM utf.user;";
        PreparedStatement resultStatement= connection.prepareStatement
                ("select * from utf.userfoodrelation where user = ? and food = ?");
        ResultSet resultSet = statement.executeQuery(sql);
        resultStatement.setString(2,food);
        while(resultSet.next()){
            String person=resultSet.getString("name");
            resultStatement.setString(1,person);
            ResultSet resultSet1=resultStatement.executeQuery();
            if(!resultSet1.next()){
                PreparedStatement ps=connection.prepareStatement
                        ("insert into utf.userfoodrelation (user, food) value (?,?)");
                ps.setString(1,person);
                ps.setString(2,food);
                ps.executeUpdate();
            }
        }
        resultSet.close();
        statement.close();
        resultStatement.close();
        connection.close();
    }


    public static void tryInsertPersonIntoFoodAndPersonRelation(String person) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://124.71.207.55:3306/utf?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true","root",DBPassword);
        Statement statement = connection.createStatement();
        String sql = "SELECT * FROM utf.dish;";
        PreparedStatement resultStatement= connection.prepareStatement
                ("select * from utf.userfoodrelation where user = ? and food = ?");
        ResultSet resultSet = statement.executeQuery(sql);
        resultStatement.setString(1,person);
        while(resultSet.next()){
            String food=resultSet.getString("name");
            resultStatement.setString(2,food);
            ResultSet resultSet1=resultStatement.executeQuery();
            if(!resultSet1.next()){
                PreparedStatement ps=connection.prepareStatement
                        ("insert into utf.userfoodrelation (user, food) value (?,?)");
                ps.setString(1,person);
                ps.setString(2,food);
                ps.executeUpdate();
            }
        }
        resultSet.close();
        statement.close();
        resultStatement.close();
        connection.close();
    }


    public static void tryInsertFoodAndFoodRelation(String x) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://124.71.207.55:3306/utf?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true","root",DBPassword);
        Statement statement = connection.createStatement();
        String sql = "SELECT * FROM utf.dish;";
        PreparedStatement resultStatement= connection.prepareStatement
                ("select * from utf.dishtodishrelation where DishOne =? and DishTwo=?");
        ResultSet resultSet = statement.executeQuery(sql);
        resultStatement.setString(1,x);
        while(resultSet.next()){
            String content=resultSet.getString("name");
            resultStatement.setString(2,content);
            ResultSet resultSet1=resultStatement.executeQuery();
            if(!resultSet1.next()){
                PreparedStatement ps=connection.prepareStatement
                        ("insert into utf.dishtodishrelation (DishOne, DishTwo) VALUE (?,?)");
                ps.setString(1,x);
                ps.setString(2,content);
                ps.executeUpdate();
            }
        }
        resultSet.close();
        statement.close();
        resultStatement.close();
        connection.close();
    }
}