package ch.heigvd.amt.resources;

import ch.heigvd.amt.database.UpdateStatus;
import ch.heigvd.amt.models.User;
import ch.heigvd.amt.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(); // used to parse JSON object

    private final UserService userService;

    @Inject
    public AccountResource(UserService userService) {
        this.userService = userService;
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    private Response createNewAccount(User receivedUser) {

        ObjectNode responseBody = JsonNodeFactory.instance.objectNode();

        // TODO: Vérfier si le password match la politique des mots de passe
        if (receivedUser.getPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[#$^+=!*()@%&]).{8,}$")) {

            // TODO: Créer le compte dans la DB
            if (userService.addUser(receivedUser).getStatus() == UpdateStatus.SUCCESS) {


                responseBody.put("username", receivedUser.getUsername());
                responseBody.put("role", receivedUser.getRole().toString());

                return Response.status(Response.Status.CREATED).entity(responseBody).build();
            }
            else {
                // on retourne l'erreur de la base de données


            }
        }
        else {

        }
    }
}
