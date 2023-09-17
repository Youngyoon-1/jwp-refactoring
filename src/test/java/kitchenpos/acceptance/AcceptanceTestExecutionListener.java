package kitchenpos.acceptance;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class AcceptanceTestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public void afterTestMethod(final TestContext testContext) {
        final JdbcTemplate jdbcTemplate = getJdbcTemplate(testContext);
        final List<String> truncateQueries = createTruncateQueries(jdbcTemplate);
        executeQueries(jdbcTemplate, truncateQueries);
    }


    private JdbcTemplate getJdbcTemplate(final TestContext testContext) {
        return testContext.getApplicationContext()
                .getBean(JdbcTemplate.class);
    }

    private List<String> createTruncateQueries(final JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.queryForList(
                "SELECT CONCAT('TRUNCATE TABLE ', TABLE_NAME, ';')"
                        + "FROM INFORMATION_SCHEMA.TABLES "
                        + "WHERE TABLE_SCHEMA = 'PUBLIC'",
                String.class
        );
    }

    private void executeQueries(final JdbcTemplate jdbcTemplate, final List<String> truncateQueries) {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE;");
        truncateQueries.forEach(jdbcTemplate::execute);
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE;");
    }
}
