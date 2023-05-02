import java.sql.*;

public class Admin extends Organiser{

    int ADMIN_ID;
    String ADMIN_EMAIL = "";
    String ADMIN_PWORD = "";

    public static String MakeConference(String confName, String[] organisers, String[] areachairs, String[] reviewers, String[] authors, int confMode) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = MakeSqlConnection();

            // Sequence of SQL queries

            // Query 1: add a new entry to the CONFERENCE table and retrieve the CONF_ID
            String sqlQuery1 = "insert into CONFERENCE(CONF_NAME, CONF_MODE) values(?, ?);";
            preparedStatement = connection.prepareStatement(sqlQuery1);
            preparedStatement.setString(1, confName);
            preparedStatement.setInt(2, confMode);
            preparedStatement.executeUpdate();

            String sqlQuery2 = " select * from CONFERENCE where CONF_NAME = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery2);
            preparedStatement.setString(1, confName);
            resultSet = preparedStatement.executeQuery();

            int confId = -1;
            while (resultSet.next()) {
                confId = resultSet.getInt("CONF_ID");
            }

            // Query 2: add the organisers
            for (int i = 0; i < organisers.length; i++) {
                String sqlQuery3 = "insert into CONF_ROLE values( ?, ?, ?);";
                preparedStatement = connection.prepareStatement(sqlQuery3);
                preparedStatement.setInt(1, confId);
                preparedStatement.setInt(2, Integer.parseInt(organisers[i]));
                preparedStatement.setString(3, "organiser");
                preparedStatement.executeUpdate();
            }

            // Query 3: add the areachairs
            for (int i = 0; i < areachairs.length; i++) {
                String sqlQuery3 = "insert into CONF_ROLE values( ?, ?, ?);";
                preparedStatement = connection.prepareStatement(sqlQuery3);
                preparedStatement.setInt(1, confId);
                preparedStatement.setInt(2, Integer.parseInt(areachairs[i]));
                preparedStatement.setString(3, "areachair");
                preparedStatement.executeUpdate();
            }

            // Query 4: add the reviewers
            for (int i = 0; i < reviewers.length; i++) {
                String sqlQuery3 = "insert into CONF_ROLE values( ?, ?, ?);";
                preparedStatement = connection.prepareStatement(sqlQuery3);
                preparedStatement.setInt(1, confId);
                preparedStatement.setInt(2, Integer.parseInt(reviewers[i]));
                preparedStatement.setString(3, "reviewer");
                preparedStatement.executeUpdate();
            }

            // Query 5: add the authors
            for (int i = 0; i < authors.length; i++) {
                String sqlQuery3 = "insert into CONF_ROLE values( ?, ?, ?);";
                preparedStatement = connection.prepareStatement(sqlQuery3);
                preparedStatement.setInt(1, confId);
                preparedStatement.setInt(2, Integer.parseInt(authors[i]));
                preparedStatement.setString(3, "author");
                preparedStatement.executeUpdate();
            }

//            System.out.println("Conference created");
            return "conference created";

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            resultSet.close();
            preparedStatement.close();
            connection.close();
        }

        return "conference not created";
    }
    public static void adminnn() {
    	
    	System.out.println("");
    	
    }
    public static void DeleteConference(int confId) throws SQLException {
        System.out.println("DeleteConference() is successful");
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = MakeSqlConnection();

            // Query1: delete all the rows that have no dependents first
            String sqlQuery1 = "delete from CONF_ROLE where CONF_ID = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery1);
            preparedStatement.setInt(1, confId);
            preparedStatement.executeUpdate();

            String sqlQuery2 = "delete from CONFERENCE where CONF_ID = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery2);
            preparedStatement.setInt(1, confId);
            preparedStatement.execute();

            System.out.println("Conference deleted");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
            connection.close();
        }
    }

    public static void RemoveMember(int confId, int userId) throws SQLException {
        System.out.println("RemoveOrganiser() is successfull");
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = MakeSqlConnection();

            String sqlQuery = "delete from conf_organiser where CONF_ID = ? and USER_ID = ?;";
            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, confId);
            preparedStatement.setInt(2, userId);
            preparedStatement.execute();

            System.out.println("Organiser removed");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
            connection.close();
        }
    }

    public static void AddMember(int confId, int userId, String role) throws SQLException {
        System.out.println("AddMember() is successful");
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = MakeSqlConnection();

            String sqlQuery = "insert into CONF_ROLE(CONF_ID, USER_ID, ROLE) values(?, ?, ?)";
            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, confId);
            preparedStatement.setInt(2, userId);
            preparedStatement.setString(3, role);
            preparedStatement.execute();

            System.out.println("Member added");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            preparedStatement.close();
            connection.close();
        }
    }

}
