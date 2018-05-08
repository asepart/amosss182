package de.fau.cs.osr.amos.asepart.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tickets")
public class Ticket
{
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "ticket_name", nullable = false)
    private String ticketName;

    @Column(name = "ticket_summary", nullable = false)
    private String ticketSummary;

    @Column(name = "ticket_description", nullable = false)
    private String ticketDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_category", nullable = false)
    private TicketCategory ticketCategory;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getTicketName()
    {
        return ticketName;
    }

    public void setTicketName(String ticketName)
    {
        this.ticketName = ticketName;
    }

    public String getTicketSummary()
    {
        return ticketSummary;
    }

    public void setTicketSummary(String ticketSummary)
    {
        this.ticketSummary = ticketSummary;
    }

    public String getTicketDescription()
    {
        return ticketDescription;
    }

    public void setTicketDescription(String ticketDescription)
    {
        this.ticketDescription = ticketDescription;
    }

    public TicketCategory getTicketCategory()
    {
        return ticketCategory;
    }

    public void setTicketCategory(TicketCategory ticketCategory)
    {
        this.ticketCategory = ticketCategory;
    }
}
