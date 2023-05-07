package FileBrowser;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Konstantinos
 */

@XmlRootElement(name = "FavFile")
@XmlAccessorType(XmlAccessType.FIELD)
public class FavFile {
    String name;
    String absolutePath;

    public String getName() {
        return name;
    }

    // @XmlElement
    public void setName(String name) {
        this.name = name;
    }

    public String getAbsolutePath() {
        return absolutePath.replace("\\", "/");
    }

    // @XmlElement
    public void setAbsolutePath(String absPath) {
        absolutePath = absPath.replace("\\", "/");
    }

}
