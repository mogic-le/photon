package de.komoot.photon.mogicext;

import com.vividsolutions.jts.geom.Point;
import de.komoot.photon.query.PhotonRequest;

public class ValidateRequest extends PhotonRequest {
    private final String plz;
    private final String ort;
    private final String stadtteil;

    ValidateRequest(String plz, String ort, String stadtteil, Integer limit, Point locationForBias, String language){
        super(null, limit, locationForBias, language);
        this.plz = plz;
        this.ort = ort;
        this.stadtteil = stadtteil;
    }

    @Override
    public String getQuery() {
        return getOrt();
    }

    public String getPlz() {
        return plz;
    }

    public String getOrt() {
        return ort;
    }

    public String getStadtteil() {
        return stadtteil;
    }
}
