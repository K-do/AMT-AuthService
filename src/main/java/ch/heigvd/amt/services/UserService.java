package ch.heigvd.amt.services;

import ch.heigvd.amt.database.UpdateResultHandler;
import org.jdbi.v3.core.Jdbi;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class UserService {

    private final Jdbi jdbi;
    private final UpdateResultHandler updateResultHandler;

    @Inject
    public UserService(Jdbi jdbi, UpdateResultHandler updateResultHandler) {
        this.jdbi = jdbi;
        this.updateResultHandler = updateResultHandler;
    }

}
