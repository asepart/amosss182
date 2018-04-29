package de.fau.cs.osr.amos.asepart.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "asepart_projects")
public class Project
{
    @Id @Column(name = "project_name")
    private String projectName;

    @Column(name = "entry_key", nullable = false)
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

    /*
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable
            (
                    name = "project_user",
                    joinColumns = { @JoinColumn(name = "project_id") },
                    inverseJoinColumns = { @JoinColumn(name = "user_id") }
            )
    private Set<User> users = new HashSet<>();

    public void addUser(User user)
    {
        users.add(user);
        user.getProjects().add(this);
    }

    public void removeUser(User user)
    {
        users.remove(user);
        user.getProjects().remove(this);
    }

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable
            (
                    name = "project_admin",
                    joinColumns = { @JoinColumn(name = "project_id") },
                    inverseJoinColumns = { @JoinColumn(name = "admin_id") }
            )
    private Set<Admin> admins = new HashSet<>();

    public void addAdmin(Admin admin)
    {
        admins.add(admin);
        admin.getProjects().add(this);
    }

    public void removeAdmin(Admin admin)
    {
        admins.remove(admin);
        admin.getProjects().remove(this);
    }
    */
}
