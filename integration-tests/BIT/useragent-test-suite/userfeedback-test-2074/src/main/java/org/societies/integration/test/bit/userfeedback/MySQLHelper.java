package org.societies.integration.test.bit.userfeedback;


import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.useragent.model.ExpProposalContent;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MySQLHelper {
    private static final Logger log = LoggerFactory.getLogger(MySQLHelper.class);

    private Connection conn;

    public MySQLHelper(String url, String username, String password) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");

        conn = DriverManager.getConnection(url, username, password);
    }

    public int clearTable(String table) throws SQLException {
        log.info("Clearing table " + table);

        Statement statement = conn.createStatement();

        int count = statement.executeUpdate("DELETE FROM `" + table + "`");

        statement.close();

        return count;
    }

    public String assertNotificationStored(int type, ExpProposalContent content) throws SQLException {
        String sql = "SELECT requestId \n" +
                "  FROM userfeedbackbean \n" +
                "  WHERE proposalText=? \n" +
                "    AND type=?";

        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, content.getProposalText());
        statement.setInt(2, type);

        ResultSet resultSet = statement.executeQuery();

        if (!resultSet.next())
            Assert.fail("No record found in database with type " + type + " and propText=[" + content.getProposalText() + "]");

        String id = resultSet.getString(1);

        sql = "SELECT `value` FROM `userfeedbackbean_options` WHERE `option_id`=?";
        statement = conn.prepareStatement(sql);
        statement.setString(1, id);

        resultSet = statement.executeQuery();

        List<String> expectedOptions = new ArrayList<String>();
        List<String> actualOptions = new ArrayList<String>();
        Collections.addAll(expectedOptions, content.getOptions());


        while (resultSet.next()) {
            actualOptions.add(resultSet.getString(1));
        }

        Tester.compareLists(expectedOptions, actualOptions);

        return id;
    }
}
