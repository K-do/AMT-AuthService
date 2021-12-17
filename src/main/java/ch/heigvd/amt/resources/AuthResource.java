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

@Path("/auth")
@ApplicationScoped
public class AuthResource {

  private static final String CREDENTIALS_ERROR = "The credentials are incorrect";
  private final UserService userService;

  @Inject
  public AuthResource(UserService userService) {
    this.userService = userService;
  }

  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response verifyLogin(User receivedUser) {

    ObjectNode responseBody = JsonNodeFactory.instance.objectNode();

    // Check if the user exists in the database
    if (userService.getUser(receivedUser.getUsername()).isPresent()) {

      User userLoggingIn = userService.getUser(receivedUser.getUsername()).get();

      // Check if the hashes match
      if (BcryptUtil.matches(receivedUser.getPassword(), userLoggingIn.getPassword())) {

        // Creation of the JWT
        String jwt =
        Jwt.issuer("AMT-AuthService").groups(userLoggingIn.getRole().toString())
                .jws()
                .algorithm(SignatureAlgorithm.ES256)
                .sign();

        responseBody.put("token", jwt);
        responseBody
            .putObject("account")
            .put("username", userLoggingIn.getUsername())
            .put("role", userLoggingIn.getRole().toString());
        return Response.status(Response.Status.OK).entity(responseBody).build();
      }
    }
    responseBody.put("error", CREDENTIALS_ERROR);
    return Response.status(Response.Status.FORBIDDEN).entity(responseBody).build();
  }
}
