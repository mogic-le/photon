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
		else if (photonRequest.hasOrt())
			filtered = filterCity(filtered, photonRequest.getOrt());

		if (photonRequest.hasCountry()) {
			System.err.println("filtering by country: "+photonRequest.getCountry());
			filtered = filterCountry(filtered, photonRequest.getCountry());
		}

		return filtered;
	}

	public TagFilterQueryBuilder buildQuery(PlzOrtRequest photonRequest) {
		TagFilterQueryBuilder builder;

		String query;
		if (photonRequest.hasOrt()) {
			query = photonRequest.getOrt();
		}
		else {
			query = photonRequest.getPlz();
		}

		builder = PhotonQueryBuilder.builder(query, photonRequest.getLanguage());
		builder.withValues("postcode");
		return builder.withLocationBias(photonRequest.getLocationForBias());
	}
}
