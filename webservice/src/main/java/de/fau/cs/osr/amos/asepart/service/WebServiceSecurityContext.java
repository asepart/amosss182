package de.fau.cs.osr.amos.asepart.service;

import java.security.Principal;
import javax.ws.rs.core.SecurityContext;

import static java.util.Objects.requireNonNull;

/**
 * Encapsulates web service security information.
 */

public class WebServiceSecurityContext implements SecurityContext
{
    private String loginName;
    private String role;
    private String scheme;

    public WebServiceSecurityContext(String loginName, String role, String scheme)
    {
        this.loginName = requireNonNull(loginName);
        this.role = requireNonNull(role);
        this.scheme = requireNonNull(scheme);
    }

    @Override
    public Principal getUserPrincipal()
    {
        return () -> loginName;
    }

    @Override
    public boolean isUserInRole(String r)
    {
        return role.equals(r);
    }

    @Override
    public boolean isSecure()
    {
        return scheme.equals("https");
    }

    @Override
    public String getAuthenticationScheme()
    {
        return SecurityContext.BASIC_AUTH;
    }
}
