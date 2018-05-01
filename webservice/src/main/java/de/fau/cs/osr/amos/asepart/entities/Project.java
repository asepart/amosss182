package de.fau.cs.osr.amos.asepart.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name = "projects")
public class Project
{
    @Id
    @Column(name = "project_name")
    private String projectName;

    @Column(name = "entry_key", nullable = false)
    private String entryKey;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable
    (
            name = "projects_users",
            joinColumns = {@JoinColumn(name = "project_name", referencedColumnName = "project_name")},
            inverseJoinColumns = {@JoinColumn(name = "user_name", referencedColumnName = "login_name")}
    )
    private Set<User> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable
    (
            name = "projects_admins",
            joinColumns = {@JoinColumn(name = "project_name", referencedColumnName = "project_name")},
            inverseJoinColumns = {@JoinColumn(name = "admin_name", referencedColumnName = "login_name")}
    )
    private Set<Admin> admins = new HashSet<>();

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

    public Set<User> getUsers()
    {
        return users;
    }

    public void setUsers(Set<User> users)
    {
        this.users = users;
    }

    public Set<Admin> getAdmins()
    {
        return admins;
    }

    public void setAdmins(Set<Admin> admins)
    {
        this.admins = admins;
    }
}
