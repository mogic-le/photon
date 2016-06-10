package de.komoot.photon.mogicext.searcher;

import de.komoot.photon.mogicext.PlzOrtRequest;
import de.komoot.photon.mogicext.query.PostcodeQueryBuilder;
import de.komoot.photon.query.PhotonQueryBuilder;
import de.komoot.photon.query.TagFilterQueryBuilder;
import de.komoot.photon.searcher.ElasticsearchSearcher;
import de.komoot.photon.searcher.PhotonRequestHandler;
import de.komoot.photon.utils.ConvertToJson;
import org.elasticsearch.action.search.SearchResponse;
import org.json.JSONObject;

import java.util.*;

public class PlzOrtPhotonRequestHandler implements PhotonRequestHandler<PlzOrtRequest> {

	private final ElasticsearchSearcher elasticsearchSearcher;

	public PlzOrtPhotonRequestHandler(ElasticsearchSearcher elasticsearchSearcher) {
		this.elasticsearchSearcher = elasticsearchSearcher;
	}

	@Override
	public final List<JSONObject> handle(PlzOrtRequest photonRequest) {
		TagFilterQueryBuilder queryBuilder = buildQuery(photonRequest);

		//we need a lo of results, because we ant to filter correct postcodes afterwards
		SearchResponse results = elasticsearchSearcher.search(queryBuilder.buildQuery(), 10000);

		List<JSONObject> resultJsonObjects = new ConvertToJson(photonRequest.getLanguage()).convert(results);

		//filter correct postcode value
		resultJsonObjects = filterPostcode(resultJsonObjects, photonRequest.getPlz());

		Integer limit = photonRequest.getLimit();
		if (resultJsonObjects.size() > limit) {
			resultJsonObjects = resultJsonObjects.subList(0, limit);
		}
		return resultJsonObjects;
	}

	/**
	 * keeps just these result element that contain the given postcode
	 */
	private List<JSONObject> filterPostcode(List<JSONObject> results, String postcode) {
		List<JSONObject> filtered = new Vector<>();

		for (JSONObject result: results) {
			if (checkPostcode(result, postcode))
				filtered.add(result);
		}

		return filtered;
	}

	/**
	 * checks, if the result element contains the correct postcode
	 */
	private boolean checkPostcode(JSONObject result, String postcode) {
		if (!result.has("properties"))
			return false;

		final Object obj = result.get("properties");
		if (!(obj instanceof JSONObject))
			return false;

		JSONObject properties = (JSONObject) obj;
		if (!properties.has("postcode"))
			return false;
		final Object pcObj = properties.get("postcode");
		if (!(pcObj instanceof String))
			return false;

		String actualPostcode = (String) pcObj;
		return (actualPostcode.equals(postcode));
	}

	public TagFilterQueryBuilder buildQuery(PlzOrtRequest photonRequest) {
		TagFilterQueryBuilder builder;

		if (photonRequest.hasOrt()) {
			builder = PhotonQueryBuilder.builder(photonRequest.getOrt(), photonRequest.getLanguage());

			Set<String> values = new HashSet<>(1);
			values.add("city");
			Map<String,Set<String>> tags = new HashMap<>();
			tags.put("place", values);
			builder = builder.withTags(tags);
		}
		else {
//			builder = PostcodeQueryBuilder.builder(photonRequest.getPlz(), photonRequest.getLanguage());

			builder = PhotonQueryBuilder.builder(photonRequest.getPlz(), photonRequest.getLanguage());
			builder.withValues("postcode");
		}

		//TODO nur, wenn gesetzt
		return builder.withLocationBias(photonRequest.getLocationForBias());
	}
}
