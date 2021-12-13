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

  @POST
  @Path("/register")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response createNewAccount(User receivedUser) {

    ObjectNode responseBody = JsonNodeFactory.instance.objectNode();

    if (receivedUser
        .getPassword()
        .matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[#$^+=!*()@%&]).{8,}$")) {

      if (userService.addUser(receivedUser).getStatus() == UpdateStatus.SUCCESS) {

        responseBody.put("username", receivedUser.getUsername());
        responseBody.put("role", receivedUser.getRole().toString());
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
