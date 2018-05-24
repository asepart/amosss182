package de.fau.cs.osr.amos.asepart.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "projects")
public class Project
{
    @Id
    @Column(name = "entry_key")
    private String entryKey;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(nullable = false)
    private String owner;

    public String getEntryKey()
    {
        return entryKey;
    }

    public void setEntryKey(String entryKey)
    {
        this.entryKey = entryKey;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }
}
