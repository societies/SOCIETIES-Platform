/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp.,
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.integration.api.selenium.rules;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.integration.api.selenium.SeleniumTest;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SqlScriptRule implements MethodRule {
    protected static final Logger log = LoggerFactory.getLogger(SqlScriptRule.class);

    private Class<? extends SeleniumTest> testClass;

    public SqlScriptRule(Class<? extends SeleniumTest> testClass) {

        this.testClass = testClass;
    }

    @Override
    public Statement apply(final Statement statement, final FrameworkMethod frameworkMethod, final Object o) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {

                String rootPath = "./platform-infrastructure/web-app/src/test/resources/sql/";
                String setupSqlFileName = rootPath + testClass.getSimpleName() + "_setup.sql";
                String teardownSqlFileName = rootPath + testClass.getSimpleName() + "_teardown.sql";

                // run the teardown script first - in case the data from the previous test is still in there
                if (new File(teardownSqlFileName).exists())
                    executeScriptFile(teardownSqlFileName);
                // run the setup script
                if (new File(setupSqlFileName).exists())
                    executeScriptFile(setupSqlFileName);

                statement.evaluate(); // perform the test

                // tear down any data we've created
                if (new File(teardownSqlFileName).exists())
                    executeScriptFile(teardownSqlFileName);
            }
        };
    }

    private void executeScriptFile(String filename) throws Throwable {
        log.debug("Executing sql script " + filename);

        List<String> statements = readStatementsFromTextFile(filename);

        Class.forName("com.mysql.jdbc.Driver");

        String url = "jdbc:mysql://localhost:3306/societiesdb";

        Connection con = DriverManager.getConnection(url, "societies", "societies");
        java.sql.Statement statement = con.createStatement();

        for (String sql : statements) {

            if (log.isTraceEnabled())
                log.trace("SQL: " + sql);

            statement.execute(sql);
        }
    }

    private List<String> readStatementsFromTextFile(String filename) throws Throwable {
        List<String> statements = new ArrayList<String>();

        Scanner scanner = new Scanner(new FileInputStream(filename));
        scanner.useDelimiter(";");

        try {
            while (scanner.hasNext()) {
                statements.add(scanner.next());
            }
        } finally {
            scanner.close();
        }

        return statements;
    }
}
