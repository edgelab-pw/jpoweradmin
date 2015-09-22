/**
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 * <p/>
 * Control panel for PowerDNS (http://powerdns.com)
 * Copyright (C) 2010 Jivko Sabev
 * Jivko Sabev (jivko.sabev@gmail.com) jsabev@nicmus.com
 *
 * @author jsabev
 */
package pw.edgelab.pdns.entities;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * Domain records bean - holds properties of a record
 * @author jsabev
 *
 */
@Entity
@Table(name = "records", indexes = {
        @Index(columnList = "name, type", name = "nametype_index"),
        @Index(columnList = "domain_id", name = "domain_id"),
        @Index(columnList = "domain_id, ordername", name = "recordorder")
})
@XmlRootElement
public class Record extends PersistentObject implements Serializable {

    private static final long serialVersionUID = 3684387704879755982L;
    private int id;
    private String name;
    private DNSType type;
    private String content;
    private int ttl;
    private int prio;
    private long change_date;
    @ColumnDefault(value = "0")
    private boolean disabled;
    private String ordername;
    @ColumnDefault(value = "1")
    private boolean auth;
    private Domain domain; //mapping back to domain

    /**
     * @return the id
     */
    @Override
    @XmlElement
    @Column(insertable = false, updatable = false)
    public int getId() {
        return this.id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    @NotNull
    @NotEmpty
    @Length(min = 1)
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    public DNSType getType() {
        return this.type;
    }

    /**
     * @param type the type to set
     */
    public void setType(DNSType type) {
        this.type = type;
    }

    /**
     * @return the content
     */
    @NotNull
    @Length(min = 1)
    @NotEmpty
    public String getContent() {
        return this.content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the ttl
     */
    @NotNull
    public int getTtl() {
        return this.ttl;
    }

    /**
     * @param ttl the ttl to set
     */
    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    /**
     * @return the prio
     */
    @NotNull
    public int getPrio() {
        return this.prio;
    }

    /**
     * @param prio the prio to set
     */
    public void setPrio(int prio) {
        this.prio = prio;
    }

    /**
     * @return the change_date
     */
    public long getChange_date() {
        return this.change_date;
    }

    /**
     * @param changeDate the change_date to set
     */
    public void setChange_date(long changeDate) {
        this.change_date = changeDate;
    }

    /**
     * @return the domain
     */
    @ManyToOne
    @XmlTransient
    public Domain getDomain() {
        return this.domain;
    }

    /**
     * @param domain the domain to set
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getOrdername() {
        return ordername;
    }

    public void setOrdername(String ordername) {
        this.ordername = ordername;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.toLowerCase().hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Record other = (Record) obj;

        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equalsIgnoreCase(other.name)) {
            return false;
        }

        if (this.name.equalsIgnoreCase(other.name) && this.content.equalsIgnoreCase(other.content) && this.type == other.type) {
            return true;
        }

        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.name + "\t" + this.type + "\t" + this.content;
    }

    @Override
    @PrePersist
    public void onCreate() {
        super.onCreate();
        this.setChange_date(this.getDateCreated().getTime());
    }

    @Override
    @PreUpdate
    public void onUpdate() {
        super.onUpdate();
        this.setChange_date(this.getDateModified().getTime());
    }

}
