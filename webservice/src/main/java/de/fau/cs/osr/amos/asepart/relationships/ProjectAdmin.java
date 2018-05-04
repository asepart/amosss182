package de.fau.cs.osr.amos.asepart.relationships;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "projects_admins")
public class ProjectAdmin
{
    @EmbeddedId ProjectAccount rel;

    public ProjectAccount getRel()
    {
        return rel;
    }

    public void setRel(ProjectAccount rel)
    {
        this.rel = rel;
    }
}
