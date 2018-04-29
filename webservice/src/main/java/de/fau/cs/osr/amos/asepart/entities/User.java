package de.fau.cs.osr.amos.asepart.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User implements Serializable
{
    @Id
    @GeneratedValue
    private Integer userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "login_name")
    private Account accountName;

    @Column(name = "phone", nullable = false)
    private String phone;

    /*
    @ManyToMany(mappedBy = "users")
    private Set<Project> projects = new HashSet<>();
    */

    public Integer getUserId()
    {
        return userId;
    }

    public Account getAccountName()
    {
        return accountName;
    }

    public void setAccountName(Account accountName)
    {
        this.accountName = accountName;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }
}
