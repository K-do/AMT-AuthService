package ch.heigvd.amt.provider;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jdbi.v3.postgres.PostgresPlugin;

/** Manage db connection lifetime */
@Singleton
public class JdbiProvider {

  private final Jdbi jdbi;

  @Inject
  public JdbiProvider(DataSource dataSource) {
    jdbi = Jdbi.create(dataSource).installPlugin(new PostgresPlugin());
    jdbi.registerArrayType(String.class, "TEXT");
  }

  /**
   * Get the jdbi singleton
   *
   * @return the jdbi singleton
   */
  @Produces
  public Jdbi jdbi() {
    return jdbi;
  }
}
