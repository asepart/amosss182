package de.fau.cs.osr.amos.asepart.relationships;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

@MappedSuperclass
@Table(name = "projects_tickets")
public class ProjectTicket
{
	@Id
    @GeneratedValue
    private Integer relId;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "ticket_id", nullable = false)
    private String ticketId;

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

    public String getTicketId()
    {
        return ticketId;
    }

    public void setTicketId(String ticketId)
    {
        this.ticketId = ticketId;
    }
}
