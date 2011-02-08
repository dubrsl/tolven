package org.tolven.doc.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@Entity
@DiscriminatorValue("ATTCH")
public class TolvenMessageWithAttachments extends TolvenMessage implements Serializable {

    private static final long serialVersionUID = 2L;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<TolvenMessageAttachment> attachments;

    public List<TolvenMessageAttachment> getAttachments() {
        if (attachments == null) {
            attachments = new ArrayList<TolvenMessageAttachment>();
        }
        return attachments;
    }

    public void setAttachments(List<TolvenMessageAttachment> attachments) {
        this.attachments = attachments;
    }

    @PrePersist
    @PreUpdate
    protected void prePersist() {
        for (TolvenMessageAttachment attachment : getAttachments()) {
            if (attachment.getParent() != this) {
                attachment.setParent(this);
            }
        }
    }
}
