package de.fau.cs.osr.amos.asepart.relationships;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "assignment")
public class Assignment
{
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "ticket_id", nullable = false)
    private Integer ticketId;

    @Column(name = "login_name", nullable = false)
    private String loginName;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getTicketId()
    {
        return ticketId;
    }

    public void setTicketId(Integer ticketId)
    {
        this.ticketId = ticketId;
    }

    public String getLoginName()
    {
        return loginName;
    }

    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
}
