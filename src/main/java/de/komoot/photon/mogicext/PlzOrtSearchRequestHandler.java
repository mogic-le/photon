package de.komoot.photon.mogicext;

import de.komoot.photon.query.BadRequestException;
import de.komoot.photon.query.PhotonRequest;
import de.komoot.photon.searcher.BaseElasticsearchSearcher;
import de.komoot.photon.searcher.PhotonRequestHandler;
import de.komoot.photon.searcher.PhotonRequestHandlerFactory;
import de.komoot.photon.utils.ConvertToGeoJson;
import org.elasticsearch.client.Client;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Liste aller PLZ/Ort-Kombinationen für Eingabe von Ort oder PLZ
 */
public class PlzOrtSearchRequestHandler<R extends PlzOrtRequest> extends Route {
    private final PlzOrtRequestFactory plzOrtRequestFactory;
    private final PlzOrtRequestHandlerFactory requestHandlerFactory;
    private final ConvertToGeoJson geoJsonConverter;

    public PlzOrtSearchRequestHandler(String path, Client esNodeClient, String languages) {
        super(path);
        Set<String> supportedLanguages = new HashSet<String>(Arrays.asList(languages.split(",")));
        this.plzOrtRequestFactory = new PlzOrtRequestFactory(supportedLanguages);
        this.geoJsonConverter = new ConvertToGeoJson();
        this.requestHandlerFactory = new PlzOrtRequestHandlerFactory(new BaseElasticsearchSearcher(esNodeClient));
    }

    @Override
    public String handle(Request request, Response response) {
        R photonRequest = null;
        try {
            photonRequest = plzOrtRequestFactory.create(request);
        } catch (BadRequestException e) {
            JSONObject json = new JSONObject();
            json.put("message", e.getMessage());
            halt(e.getHttpStatus(), json.toString());
        }

        PhotonRequestHandler<R> handler = requestHandlerFactory.createHandler(photonRequest);
        List<JSONObject> results = handler.handle(photonRequest);
        JSONObject geoJsonResults = geoJsonConverter.convert(results);
        response.type("application/json; charset=utf-8");
        response.header("Access-Control-Allow-Origin", "*");
        if (request.queryParams("debug") != null)
            return geoJsonResults.toString(4);

        return geoJsonResults.toString();
    }
}
