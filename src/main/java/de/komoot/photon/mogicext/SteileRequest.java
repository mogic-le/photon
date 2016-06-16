package de.komoot.photon.mogicext;

import com.vividsolutions.jts.geom.Point;
import de.komoot.photon.query.PhotonRequest;

public class SteileRequest extends ExtPhotonRequest {
    private final String plz;
    private final String ort;

    SteileRequest(String plz, String ort, Integer limit, Point locationForBias, String language){
        super(limit, locationForBias, language);
        this.plz = plz;
        this.ort = ort;
    }

    @Override
    public String getQuery() {
        return getOrt();
    }

    public boolean hasPlz() {
        return plz != null && !plz.isEmpty();
    }

    public String getPlz() {
        return plz;
    }

    public boolean hasOrt() {
        return ort != null && !ort.isEmpty();
    }

    public String getOrt() {
        return ort;
    }
}