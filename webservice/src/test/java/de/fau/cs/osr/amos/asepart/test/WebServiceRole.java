package de.fau.cs.osr.amos.asepart.test;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.PreMatching;

import static java.util.Objects.requireNonNull;

@PreMatching
public class WebServiceRole implements ClientRequestFilter
{
    private final String role;

    private WebServiceRole(String role)
    {
        this.role = requireNonNull(role);
    }

    static WebServiceRole admin()
    {
        return new WebServiceRole("Admin");
    }

    static WebServiceRole user()
    {
        return new WebServiceRole("User");
    }

    @Override
    public void filter(ClientRequestContext requestContext)
    {
        requestContext.getHeaders().putSingle("X-ASEPART-Role", role);
    }
}
