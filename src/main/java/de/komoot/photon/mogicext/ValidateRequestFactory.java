package de.komoot.photon.mogicext;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import de.komoot.photon.query.BadRequestException;
import de.komoot.photon.query.LanguageChecker;
import spark.Request;

import java.util.Set;

/**
 * A factory that creates a {@link ValidateRequest} from a {@link Request web request}
 */
public class ValidateRequestFactory {
    private final LanguageChecker languageChecker;
    private final static GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public ValidateRequestFactory(Set<String> supportedLanguages) {
        this.languageChecker = new LanguageChecker(supportedLanguages);
    }

    public <R extends ValidateRequest> R create(Request webRequest) throws BadRequestException {
        String language = webRequest.queryParams("lang");
        language = language == null ? "en" : language;
        languageChecker.apply(language);

        String plz = webRequest.queryParams("plz");
        String ort = webRequest.queryParams("ort");
        String steil = webRequest.queryParams("steil");
        if (plz == null || ort == null || steil == null) throw new BadRequestException(400, "missing search term 'plz', 'ort' or 'steil', specify all of them");

        Integer limit;
        try {
            limit = Integer.valueOf(webRequest.queryParams("limit"));
        } catch (NumberFormatException e) {
            limit = 15;
        }

        Point locationForBias = null;
        try {
            Double lon = Double.valueOf(webRequest.queryParams("lon"));
            Double lat = Double.valueOf(webRequest.queryParams("lat"));
            locationForBias = geometryFactory.createPoint(new Coordinate(lon, lat));
        } catch (Exception nfe) {
            //ignore
        }

        return (R) new ValidateRequest(plz, ort, steil, limit, locationForBias, language);
    }
}
