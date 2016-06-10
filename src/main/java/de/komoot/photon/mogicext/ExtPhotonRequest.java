package de.komoot.photon.mogicext;

import com.vividsolutions.jts.geom.Point;
import de.komoot.photon.query.PhotonRequest;

public abstract class ExtPhotonRequest extends PhotonRequest {
    private String country;

    ExtPhotonRequest(Integer limit, Point locationForBias, String language){
        super(null, limit, locationForBias, language);
    }

    abstract public String getQuery();

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean hasCountry() {
        return country != null && !country.isEmpty();
    }

    public String getCountry() {
        return country;
    }
}
