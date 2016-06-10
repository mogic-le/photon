package de.komoot.photon.mogicext.searcher;

import de.komoot.photon.mogicext.PlzOrtRequest;
import de.komoot.photon.query.PhotonQueryBuilder;
import de.komoot.photon.query.TagFilterQueryBuilder;
import de.komoot.photon.searcher.ElasticsearchSearcher;
import de.komoot.photon.searcher.PhotonRequestHandler;
import org.json.JSONObject;

import java.util.*;

public class PlzOrtPhotonRequestHandler extends PhotonRequestHandlerBase<PlzOrtRequest> implements PhotonRequestHandler<PlzOrtRequest> {

	public PlzOrtPhotonRequestHandler(ElasticsearchSearcher elasticsearchSearcher) {
		super(elasticsearchSearcher);
	}

	@Override
	protected List<JSONObject> filterResult(List<JSONObject> results, PlzOrtRequest photonRequest) {
		List<JSONObject> filtered = results;

		if (photonRequest.hasPlz())
			filtered = filterPostcode(filtered, photonRequest.getPlz());

		if (photonRequest.hasCountry()) {
			System.err.println("filtering by country: "+photonRequest.getCountry());
			filtered = filterCountry(filtered, photonRequest.getCountry());
		}

		return filtered;
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
			builder = PhotonQueryBuilder.builder(photonRequest.getPlz(), photonRequest.getLanguage());
			builder.withValues("postcode");
		}

		return builder.withLocationBias(photonRequest.getLocationForBias());
	}
}
