package de.fau.cs.osr.amos.asepart.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "messages")
public class Message
{
    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    private String content;

    @Column(name = "ticket_id", nullable = false)
    private Integer ticketId;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getSender()
    {
        return sender;
    }

    public void setSender(String sender)
    {
        this.sender = sender;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
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
