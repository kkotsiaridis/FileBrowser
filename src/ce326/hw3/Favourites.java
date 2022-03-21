/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ce326.hw3;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Konstantinos
 */


@XmlRootElement(name = "Favourites")
@XmlAccessorType (XmlAccessType.FIELD)
public class Favourites {
    
    @XmlElement(name = "FavFile")
    private List<FavFile> favourites = null;
 
    public List<FavFile> getFavourites() {
        return favourites;
    }
 
    public void setFavourites(List<FavFile> favList) {
        this.favourites = favList;
    }
}