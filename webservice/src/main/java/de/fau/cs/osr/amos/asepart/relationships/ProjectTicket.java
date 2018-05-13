package de.fau.cs.osr.amos.asepart.relationships;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "projects_tickets")
public class ProjectTicket
{
    @Id
    @GeneratedValue
    private Integer relId;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "ticket_id", nullable = false)
    private Integer ticketId;

    public Integer getRelId()
    {
        return relId;
    }

    public void setRelId(Integer relId)
    {
        this.relId = relId;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public Integer getTicketId()
    {
        return ticketId;
    }

    public void setTicketId(Integer ticketId)
    {
        this.ticketId = ticketId;
    }
}
