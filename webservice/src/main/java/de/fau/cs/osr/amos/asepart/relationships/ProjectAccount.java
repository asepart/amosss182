package de.fau.cs.osr.amos.asepart.relationships;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ProjectAccount
{
    @Id
    @GeneratedValue
    private Integer relId;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "login_name", nullable = false)
    private String loginName;

    public Integer getRelId()
    {
        return relId;
    }

    public void setRelId(Integer relId)
    {
        this.relId = relId;
    }

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
