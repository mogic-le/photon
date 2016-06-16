package de.komoot.photon.mogicext.searcher;

import de.komoot.photon.query.PhotonRequest;
import de.komoot.photon.query.TagFilterQueryBuilder;
import de.komoot.photon.searcher.ElasticsearchSearcher;
import de.komoot.photon.searcher.PhotonRequestHandler;
import de.komoot.photon.searcher.StreetDupesRemover;
import de.komoot.photon.utils.ConvertToJson;
import org.elasticsearch.action.search.SearchResponse;
import org.json.JSONObject;

import java.util.List;
import java.util.Vector;

/**
 * Created by Sachin Dole on 2/20/2015.
 */
public abstract class PhotonRequestHandlerBase<R extends PhotonRequest> implements PhotonRequestHandler<R> {

    private final ElasticsearchSearcher elasticsearchSearcher;

    public PhotonRequestHandlerBase(ElasticsearchSearcher elasticsearchSearcher) {
        this.elasticsearchSearcher = elasticsearchSearcher;
    }        

    @Override
    public final List<JSONObject> handle(R photonRequest) {
        TagFilterQueryBuilder queryBuilder = buildQuery(photonRequest);
        Integer limit = photonRequest.getLimit();
        SearchResponse results = elasticsearchSearcher.search(queryBuilder.buildQuery(), limit);
        if (results.getHits().getTotalHits() == 0) {
            results = elasticsearchSearcher.search(queryBuilder.withLenientMatch().buildQuery(), limit);
        }
        List<JSONObject> resultJsonObjects = new ConvertToJson(photonRequest.getLanguage()).convert(results);

        //filter correct postcode value
        resultJsonObjects = filterResult(resultJsonObjects, photonRequest);

        StreetDupesRemover streetDupesRemover = new StreetDupesRemover(photonRequest.getLanguage());
        resultJsonObjects = streetDupesRemover.execute(resultJsonObjects);
        if (resultJsonObjects.size() > limit) {
            resultJsonObjects = resultJsonObjects.subList(0, limit);
        }
        return resultJsonObjects;
    }

    /**
     * keeps just these result element that contain the given postcode (substring)
     */
    protected List<JSONObject> filterPostcode(List<JSONObject> results, String postcode) {
        List<JSONObject> filtered = new Vector<>();

        for (JSONObject result: results) {
            if (propertyStartingWith(result, "postcode", postcode))
                filtered.add(result);
        }

        return filtered;
    }

    /**
     * keeps just these result elements whose names start with the given city name.
     * If a city property exists, it has to start with the requested name, too.
     */
    protected List<JSONObject> filterNameAndCity(List<JSONObject> results, String city) {
        List<JSONObject> filtered = new Vector<>();

        for (JSONObject result: results) {
            //wenn city vorhanden, aber nicht dem gesuchten entspricht, dann auslassen
            String propCity = getProperty(result, "city");
            if (propCity != null && !propCity.toLowerCase().startsWith(city.toLowerCase()))
                continue;

            //ansonsten city ignorieren und auf name matchen
            if (propertyStartingWith(result, "name", city))
                filtered.add(result);
        }

        return filtered;
    }

    /**
     * keeps just these result elements that contain the given city (substring)
     */
    protected List<JSONObject> filterCity(List<JSONObject> results, String city) {
        List<JSONObject> filtered = new Vector<>();

        for (JSONObject result: results) {
            if (propertyStartingWith(result, "city", city))
                filtered.add(result);
        }

        return filtered;
    }

    /**
     * keeps just these result element that contain the given country
     */
    protected List<JSONObject> filterCountry(List<JSONObject> results, String country) {
        List<JSONObject> filtered = new Vector<>();

        for (JSONObject result: results) {
            if (propertyMatching(result, "country", country))
                filtered.add(result);
        }

        return filtered;
    }

    /**
     * checks, if the result element contains the correct property value (case ignoring)
     */
    protected boolean propertyMatching(JSONObject result, String property, String value) {
        if (!result.has("properties"))
            return false;

        final Object obj = result.get("properties");
        if (!(obj instanceof JSONObject))
            return false;

        JSONObject properties = (JSONObject) obj;
        if (!properties.has(property))
            return false;
        final Object pcObj = properties.get(property);
        if (!(pcObj instanceof String))
            return false;

        String actualValue = (String) pcObj;
        return (actualValue.equalsIgnoreCase(value));
    }

    /**
     * checks, if the result element contains a property value starts with the given value (case ignoring)
     */
    protected boolean propertyStartingWith(JSONObject result, String property, String value) {
        if (!result.has("properties"))
            return false;

        final Object obj = result.get("properties");
        if (!(obj instanceof JSONObject))
            return false;

        JSONObject properties = (JSONObject) obj;
        if (!properties.has(property))
            return false;
        final Object pcObj = properties.get(property);
        if (!(pcObj instanceof String))
            return false;

        String actualValue = ((String) pcObj).toLowerCase();
        return (actualValue.startsWith(value.toLowerCase()));
    }

    protected String getProperty(JSONObject result, String property) {
        if (!result.has("properties"))
            return null;

        final Object obj = result.get("properties");
        if (!(obj instanceof JSONObject))
            return null;

        JSONObject properties = (JSONObject) obj;
        if (!properties.has(property))
            return null;
        final Object pcObj = properties.get(property);
        if (!(pcObj instanceof String))
            return null;

        return ((String) pcObj);
    }

    protected abstract List<JSONObject> filterResult(List<JSONObject> results, R photonRequest);

    /**
     * Given a {@link PhotonRequest photon request}, build a {@link TagFilterQueryBuilder photon specific query builder} that can be used in the {@link
     * PhotonRequestHandlerBase#handle handle} method to execute the search.
     */
    protected abstract TagFilterQueryBuilder buildQuery(R photonRequest);
}