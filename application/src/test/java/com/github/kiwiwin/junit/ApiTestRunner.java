package com.github.kiwiwin.junit;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.rules.TestRule;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ApiTestRunner extends InjectorBasedRunner {
    private final TestRule resetMock = (base, description) -> new Statement() {

        @Override
        public void evaluate() throws Throwable {
            base.evaluate();
        }
    };
    @Inject
    private SqlSessionFactory sessionFactory;
    private final TestRule cleanData = (base, description) -> new Statement() {

        @Override
        public void evaluate() throws Throwable {
            cleanData();
            base.evaluate();
        }
    };

    public ApiTestRunner(final Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    private void cleanData() throws SQLException {
        SqlSession sqlSession = sessionFactory.openSession();
        Connection connection = sqlSession.getConnection();
        java.sql.Statement statement = connection.createStatement();
        statement.executeUpdate("DELETE FROM USERS");

        statement.close();
        connection.commit();
        connection.close();
        sqlSession.close();
    }

    @Override
    protected List<TestRule> getTestRules(Object target) {
        List<TestRule> rules = new ArrayList<>();
        rules.add(resetMock);
        rules.add(cleanData);
        rules.addAll(super.getTestRules(target));
        return rules;
    }
}