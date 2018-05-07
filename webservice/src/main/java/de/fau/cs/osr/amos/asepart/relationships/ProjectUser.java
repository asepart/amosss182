package de.fau.cs.osr.amos.asepart.relationships;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "projects_users")
public class ProjectUser extends ProjectAccount
{
}
