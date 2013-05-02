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

        List<String> options = new ArrayList<String>();
        Collections.addAll(options, content.getOptions());

        while (resultSet.next()) {
            if (!options.contains(resultSet.getString(1)))
                Assert.fail("Option [" + resultSet.getString(1) + "] was found in result set, but not expected");

            options.remove(options.indexOf(resultSet.getString(1)));
        }

        if (options.size() > 0)
            Assert.fail("Option [" + options.get(0) + "] was not found in result set");

        return id;
    }
}
