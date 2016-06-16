package de.komoot.photon.mogicext.searcher;

import de.komoot.photon.mogicext.ValidateRequest;
import de.komoot.photon.query.PhotonQueryBuilder;
import de.komoot.photon.query.TagFilterQueryBuilder;
import de.komoot.photon.searcher.ElasticsearchSearcher;
import de.komoot.photon.searcher.PhotonRequestHandler;
import org.json.JSONObject;

import java.util.List;
import java.util.Vector;

public class ValidatePhotonRequestHandler extends PhotonRequestHandlerBase<ValidateRequest> implements PhotonRequestHandler<ValidateRequest> {

	public ValidatePhotonRequestHandler(ElasticsearchSearcher elasticsearchSearcher) {
		super(elasticsearchSearcher);
	}

	@Override
	protected List<JSONObject> filterResult(List<JSONObject> results, ValidateRequest photonRequest) {
		List<JSONObject> filtered = new Vector<>();

		for (JSONObject result: results) {
			final boolean postcodeValid = propertyStartingWith(result, "postcode", photonRequest.getPlz());
			final boolean cityValid = propertyMatching(result, "city", photonRequest.getOrt());
			if (postcodeValid && cityValid)
				filtered.add(result);
		}

		if (photonRequest.hasCountry())
			filtered = filterCountry(filtered, photonRequest.getCountry());

		return filtered;
	}

	@Override
	public TagFilterQueryBuilder buildQuery(ValidateRequest photonRequest) {
		TagFilterQueryBuilder builder = PhotonQueryBuilder.builder(photonRequest.getStadtteil(), photonRequest.getLanguage());
		builder.withValues("suburb");

		return builder.withLocationBias(photonRequest.getLocationForBias());
	}
}