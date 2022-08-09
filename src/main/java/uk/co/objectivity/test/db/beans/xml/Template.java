package uk.co.objectivity.test.db.beans.xml;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "template")
@XmlAccessorType(XmlAccessType.FIELD)
public class Template {
    @XmlAttribute
    private boolean replaceOn = true;

    @XmlElement(name = "replace")
    private List<Replace> replaces = new ArrayList<Replace>();

    public List<Replace> getReplaces() {
        return replaces;
    }

    public void setReplaces(List<Replace> replaces) {
        this.replaces = replaces;
    }

    public boolean isReplaceOn() {
        return replaceOn;
    }

    public void setReplaceOn(boolean replaceOn) {
        this.replaceOn = replaceOn;
    }

    @Transient
    public void filter() {
        // trim
        // filter the name be null or empty, the value be null
        if (replaces == null) {
            return;
        }
        replaces = replaces.stream().filter(repl -> repl.getName() != null && repl.getValue() != null).map(repla -> {
            repla.setName(repla.getName().trim());
            repla.setValue(repla.getValue().trim());
            return repla;
        }).filter(repl -> !repl.getName().isEmpty()).collect(Collectors.toList());
    }

}
