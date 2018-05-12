package de.fau.cs.osr.amos.asepart.relationships;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "projects_tickets")
public class ProjectTicket
{
	@Id
    private Integer relId;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "ticket_name", nullable = false)
    private String ticketName;

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

    public String getTicketName()
    {
        return ticketName;
    }

    public void setTicketName(String ticketName)
    {
        this.ticketName = ticketName;
    }
}
