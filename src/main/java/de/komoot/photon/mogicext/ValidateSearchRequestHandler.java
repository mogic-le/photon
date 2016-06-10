package de.komoot.photon.mogicext;

import de.komoot.photon.query.BadRequestException;
import de.komoot.photon.searcher.BaseElasticsearchSearcher;
import de.komoot.photon.searcher.PhotonRequestHandler;
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
 * Prüfung von PLZ/Ort/Stadtteil Kombination auf Gültigkeit
 */
public class ValidateSearchRequestHandler<R extends ValidateRequest> extends Route {
    private final ValidateRequestFactory validateRequestFactory;
    private final ValidateRequestHandlerFactory requestHandlerFactory;

    public ValidateSearchRequestHandler(String path, Client esNodeClient, String languages) {
        super(path);
        Set<String> supportedLanguages = new HashSet<String>(Arrays.asList(languages.split(",")));
        this.validateRequestFactory = new ValidateRequestFactory(supportedLanguages);
        this.requestHandlerFactory = new ValidateRequestHandlerFactory(new BaseElasticsearchSearcher(esNodeClient));
    }

    @Override
    public String handle(Request request, Response response) {
        R photonRequest = null;
        try {
            photonRequest = validateRequestFactory.create(request);
        } catch (BadRequestException e) {
            JSONObject json = new JSONObject();
            json.put("message", e.getMessage());
            halt(e.getHttpStatus(), json.toString());
        }

        PhotonRequestHandler<R> handler = requestHandlerFactory.createHandler(photonRequest);

        List<JSONObject> results = handler.handle(photonRequest);
        boolean valid = (results.size() > 0);

        //return bool as json
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("valid", valid);

        response.type("application/json; charset=utf-8");
        response.header("Access-Control-Allow-Origin", "*");
        if (request.queryParams("debug") != null)
            return jsonResult.toString(4);

        return jsonResult.toString();
    }
}
