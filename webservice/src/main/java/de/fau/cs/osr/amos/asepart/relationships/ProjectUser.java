package de.fau.cs.osr.amos.asepart.relationships;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "projects_users")
public class ProjectUser extends ProjectAccount
{
    @Column(nullable = false)
    private Boolean joined = false;

    public Boolean getJoined()
    {
        return joined;
    }

    public void setJoined(Boolean joined)
    {
        this.joined = joined;
    }
}
