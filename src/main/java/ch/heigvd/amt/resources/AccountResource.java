package ch.heigvd.amt.resources;

import ch.heigvd.amt.database.UpdateStatus;
import ch.heigvd.amt.models.User;
import ch.heigvd.amt.services.UserService;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/** Class managing accounts creation related route */
@Path("/accounts")
@ApplicationScoped
public class AccountResource {

  private static final String PASSWORD_ERROR =
      "The password does not match the security politics, "
          + "it should be at least 8 char long, should contain at least one uppercase char, one lowercase char, "
          + "one digit and one special character";
  private static final String USER_ERROR = "The username already exist";

  private final UserService userService;

  @Inject
  public AccountResource(UserService userService) {
    this.userService = userService;
  }

  /**
   * Method to create a new account
   *
   * @param userSigningUp user trying to create an account
   * @return a response body with a status code
   */
  @POST
  @Path("/register")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response createNewAccount(User userSigningUp) {

    // Starts to create the response body
    ObjectNode responseBody = JsonNodeFactory.instance.objectNode();

    // Controls restriction with the password. If it doesn't match, the response body will contain
    // an error
    if (userSigningUp
        .getPassword()
        .matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[§\\]°\\-`´\\[¬{}_£€<>#$^+=!?*()@%&|¦ç/'\"¢~:;,.¨]).{8,}$")) {

      // Give the base role to the user
      userSigningUp.setRole(User.Role.MEMBER);

      // If the user is successfully created, the response body will contain the username and the
      // role. If not, it
      // will contain an error
      if (userService.addUser(userSigningUp).getStatus() == UpdateStatus.SUCCESS) {
        responseBody.put("username", userSigningUp.getUsername());
        responseBody.put("role", userSigningUp.getRole().toString());
        return Response.status(Response.Status.CREATED).entity(responseBody).build();
      } else {
        responseBody.put("error", USER_ERROR);
        return Response.status(Response.Status.CONFLICT).entity(responseBody).build();
      }
    } else {
      responseBody
          .putArray("errors")
          .add(
              JsonNodeFactory.instance
                  .objectNode()
                  .put("property", "password")
                  .put("message", PASSWORD_ERROR));
      return Response.status(Response.Status.BAD_REQUEST).entity(responseBody).build();
    }
  }
}
