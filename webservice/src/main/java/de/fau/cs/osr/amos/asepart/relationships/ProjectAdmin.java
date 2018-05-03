package de.fau.cs.osr.amos.asepart.relationships;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "projects_admins")
public class ProjectAdmin
{
    @EmbeddedId ProjectAccount relId;

    public ProjectAccount getRelId()
    {
        return relId;
    }

    public void setRelId(ProjectAccount relId)
    {
        this.relId = relId;
    }
}
