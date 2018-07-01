package de.fau.cs.osr.amos.asepart.ext;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * This class is used to include exception messages
 * in server error response. Only suited for debugging, do not
 * use it in production!
 */

@Provider
public class DebugExceptionMapper implements ExceptionMapper<Exception>
{
    @Override
    public Response toResponse(Exception exception)
    {
        exception.printStackTrace();
        return Response.serverError().entity(exception.getMessage()).build();
    }
}
