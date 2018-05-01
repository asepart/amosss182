package de.fau.cs.osr.amos.asepart.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "asepart_admins")
public class Admin extends Account
{
    /*
    @ManyToMany(mappedBy = "admins")
    private Set<Project> projects = new HashSet<>();
    */

}
