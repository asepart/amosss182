package de.fau.cs.osr.amos.asepart.relationships;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "projects_users")
public class ProjectUser
{
    @EmbeddedId
    ProjectAccount rel;

    public ProjectAccount getRel()
    {
        return rel;
    }

    public void setRel(ProjectAccount rel)
    {
        this.rel = rel;
    }
}
