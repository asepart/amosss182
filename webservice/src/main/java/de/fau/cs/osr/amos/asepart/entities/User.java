package de.fau.cs.osr.amos.asepart.entities;

import java.util.Set;
import java.util.HashSet;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class User
{
    @Id @Column(name = "user_id")
    @GeneratedValue
    private int userID;

    @Id @Column(name = "user_name")
    private String userName;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @ManyToMany(mappedBy = "users")
    private Set<Project> projects = new HashSet<>();

    public int getUserID()
    {
        return userID;
    }

    public void setUserID(int userID)
    {
        this.userID = userID;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getPasswordHash()
    {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash)
    {
        this.passwordHash = passwordHash;
    }

    public Set<Project> getProjects()
    {
        return projects;
    }

    public void setProjects(Set<Project> projects)
    {
        this.projects = projects;
    }
}
