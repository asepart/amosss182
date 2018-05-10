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
    @Column(name = "project_name")
    private String projectName;

    @Column(name = "entry_key", nullable = false, unique = true)
    private String entryKey;

    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public String getEntryKey()
    {
        return entryKey;
    }

    public void setEntryKey(String entryKey)
    {
        this.entryKey = entryKey;
    }
}
