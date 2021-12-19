package ch.heigvd.amt.resources;

import ch.heigvd.amt.models.User;
import ch.heigvd.amt.services.UserService;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.algorithm.SignatureAlgorithm;
import io.smallrye.jwt.build.Jwt;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/** Class allowing to treat requests from the application server when a member tries to log in */
@Path("/auth")
@ApplicationScoped
public class AuthResource {

  private static final String CREDENTIALS_ERROR =
      "The credentials are incorrect"; // Error message returned to the application server
  private final UserService userService;

  @Inject
  public AuthResource(UserService userService) {
    this.userService = userService;
  }

  /**
   * Method allowing to treat requests from the application server when a member tries to log in
   *
   * @param userLoggingIn User trying to log in
   * @return Response depending on whether the member was able to log in or not
   */
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response verifyLogin(User userLoggingIn) {

    ObjectNode responseBody = JsonNodeFactory.instance.objectNode();

    // Check if the user exists in the database
    if (userService.getUser(userLoggingIn.getUsername()).isPresent()) {

      User userFromDB =
          userService
              .getUser(userLoggingIn.getUsername())
              .get(); // User retrieved in DB according to the username

      // Check if the hashes match
      if (BcryptUtil.matches(userLoggingIn.getPassword(), userFromDB.getPassword())) {

        // Creation of the JWT
        String jwt =
            Jwt.issuer("AMT-AuthService")
                .subject(userFromDB.getUsername())
                .groups(userFromDB.getRole().toString())
                .jws()
                .algorithm(SignatureAlgorithm.ES256) // Signing the JWT with ES256 algorithm
                .sign();

        responseBody.put("token", jwt);
        responseBody
            .putObject("account")
            .put("username", userFromDB.getUsername())
            .put("role", userFromDB.getRole().toString());
        return Response.status(Response.Status.OK).entity(responseBody).build();
      }
    }
    responseBody.put("error", CREDENTIALS_ERROR);
    return Response.status(Response.Status.FORBIDDEN).entity(responseBody).build();
  }
}
