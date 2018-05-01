package de.fau.cs.osr.amos.asepart.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "asepart_users")
public class User extends Account
{
    @Column(name = "phone", nullable = false)
    private String phone;

    /*
    @ManyToMany(mappedBy = "users")
    private Set<Project> projects = new HashSet<>();
    */

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }
}
