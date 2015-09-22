package pw.edgelab.pdns.entities;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Created by edgelab on 22/09/15.
 */
@Entity
@Table(name = "comments", indexes = {
        @Index(columnList = "domain", name = "comments_domain_id_idx"),
        @Index(columnList = "name, type,", name = "comments_name_type_idx"),
        @Index(columnList = "comments_order_idx", name = "domain_id, modified_at")
})
public class Comment extends PersistentObject {

    @NotNull
    @Length(max = 255)
    private String name;
    @NotNull
    @Length(max = 10)
    private String type;
    @NotNull
    private int modified_at;
    @NotNull
    @Length(max = 40)
    private String account;
    @NotNull
    @Length(max = 64000)
    private String comment;
    @ManyToOne
    private Domain domain;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getModified_at() {
        return modified_at;
    }

    public void setModified_at(int modified_at) {
        this.modified_at = modified_at;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }
}
