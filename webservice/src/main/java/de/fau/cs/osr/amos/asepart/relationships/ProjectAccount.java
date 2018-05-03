package de.fau.cs.osr.amos.asepart.relationships;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import static java.util.Objects.requireNonNull;

@Embeddable
public class ProjectAccount implements Serializable
{
    public ProjectAccount(String projectName, String loginName)
    {
        this.projectName = requireNonNull(projectName);
        this.loginName = requireNonNull(loginName);
    }

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "login_name")
    private String loginName;

    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public String getLoginName()
    {
        return loginName;
    }

    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
}
