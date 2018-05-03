package de.fau.cs.osr.amos.asepart.relationships;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ProjectAccount implements Serializable
{
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
