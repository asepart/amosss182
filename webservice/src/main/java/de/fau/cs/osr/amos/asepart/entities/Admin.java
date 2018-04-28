package de.fau.cs.osr.amos.asepart.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;

@Entity
@Table(name = "admins")
public class Admin
{
    @Id @Column
    private String username;

    @Column(name = "password_hash")
    private String passwordHash;

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPasswordHash()
    {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash)
    {
        this.passwordHash = passwordHash;
    }
}
