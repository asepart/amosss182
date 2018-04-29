package de.fau.cs.osr.amos.asepart.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "admins")
public class Admin implements Serializable
{
    @Id
    @GeneratedValue
    private Integer adminId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "login_name")
    private Account accountName;

    /*
    @ManyToMany(mappedBy = "admins")
    private Set<Project> projects = new HashSet<>();
    */

    public Integer getAdminId()
    {
        return adminId;
    }

    public Account getAccountName()
    {
        return accountName;
    }

    public void setAccountName(Account accountName)
    {
        this.accountName = accountName;
    }
}
