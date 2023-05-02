import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.xdevapi.JsonArray;
import org.json.*;

public class User {
    public int USER_ID;
    public String USER_NAME = "";
    public String USER_EMAIL = "";
    public String USER_PWORD = "";
    public String USER_EXPERTISE;
    public String USER_DOMAIN;
    public List<String> USER_ROLES = new ArrayList<>();
    public List<Conference> USER_CONF = new ArrayList<>();
    public void GetUser(int userId) throws SQLException {
    

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        

        try {
            connection = Role.MakeSqlConnection();

            // Get the user details
            String sqlQuery1 = "select * from USER where USER_ID = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery1);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
            {
                USER_ID = resultSet.getInt("USER_ID");
                USER_NAME = resultSet.getString("USER_NAME");
                USER_EMAIL = resultSet.getString("USER_EMAIL");
                USER_EXPERTISE = resultSet.getString("USER_EXPERTISE");
                USER_DOMAIN = resultSet.getString("USER_DOMAIN");
            }

            // Get user roles
            String sqlQuery4 = "select * from CONF_ROLE where USER_ID = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery4);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            List<Integer> conferencesList = new ArrayList<>();
            while (resultSet.next())
            {
                USER_ROLES.add(String.valueOf(resultSet.getInt("CONF_ID")) +  "," + resultSet.getString("ROLE"));

                if (!conferencesList.contains(resultSet.getInt("CONF_ID")))
                {
                    conferencesList.add(resultSet.getInt("CONF_ID"));
                }
            }

            // Get user conference
            String sqlQuery5 = "select * from CONFERENCE where CONF_ID = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery5);

            for (int confId : conferencesList)
            {
                Conference conference = new Conference();
                preparedStatement.setInt(1, confId);
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next())
                {
                    conference.CONF_ID = resultSet.getInt("CONF_ID");
                    conference.CONF_NAME = resultSet.getString("CONF_NAME");
                    conference.CONF_MODE = resultSet.getInt("CONF_MODE");
                }
                USER_CONF.add(conference);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
            connection.close();
        }
    }
    public static String JSONUserData(int USER_ID) throws SQLException
    {
        User user = new User();
        user.GetUser(USER_ID);

        JSONObject userObj = new JSONObject();
        userObj.put("userId", user.USER_ID);
        userObj.put("userName", user.USER_NAME);
        userObj.put("userEmail", user.USER_EMAIL);
        userObj.put("userDom", user.USER_DOMAIN);

        JSONArray userExpertise = new JSONArray();
        String[] expertiseStrArr = user.USER_EXPERTISE.split(",");
        for (String expertise : expertiseStrArr)
        {
            userExpertise.put(expertise);
        }
        userObj.put("userExpertise", userExpertise);

        JSONArray userConf = new JSONArray();
        for (Conference conference : user.USER_CONF)
        {
            JSONObject confObj = new JSONObject();
            confObj.put("CONF_ID", conference.CONF_ID);
            confObj.put("CONF_NAME", conference.CONF_NAME);
            confObj.put("CONF_MODE", conference.CONF_MODE);

            userConf.put(confObj);
        }
        userObj.put("userConf", userConf);

        return ">>" + userObj.toString();
    }
    
    public static void userss() {
    	
    	System.out.print("");
    }
    

    public static boolean CreateUser(String user_name, String user_email, String user_expertise, String user_domain, String user_pword) throws SQLException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = Role.MakeSqlConnection();

            // Sequence of SQL queries
            // Query 1: add a new entry to the USER table and retrieve all attributes
            String sqlQuery1 = "insert into USER(USER_NAME,USER_EMAIL,USER_EXPERTISE,USER_DOMAIN,USER_PWORD) values(?,?,?,?,?);";
            preparedStatement = connection.prepareStatement(sqlQuery1);
            preparedStatement.setString(1, user_name);
            preparedStatement.setString(2, user_email);
            preparedStatement.setString(3, user_expertise);
            preparedStatement.setString(4, user_domain);
            preparedStatement.setString(5, user_pword);

            preparedStatement.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
//            resultSet.close();
            preparedStatement.close();
            connection.close();
        }

        return false;
    }
}
