package de.komoot.photon.mogicext.searcher;

import de.komoot.photon.mogicext.SteileRequest;
import de.komoot.photon.query.PhotonQueryBuilder;
import de.komoot.photon.query.TagFilterQueryBuilder;
import de.komoot.photon.searcher.ElasticsearchSearcher;
import de.komoot.photon.searcher.PhotonRequestHandler;
import org.json.JSONObject;

import java.util.List;

public class SteilePhotonRequestHandler extends PhotonRequestHandlerBase<SteileRequest> implements PhotonRequestHandler<SteileRequest> {

	public SteilePhotonRequestHandler(ElasticsearchSearcher elasticsearchSearcher) {
		super(elasticsearchSearcher);
	}

	@Override
	protected List<JSONObject> filterResult(List<JSONObject> results, SteileRequest photonRequest) {
		List<JSONObject> filtered = results;

		if (photonRequest.hasOrt())
			filtered = filterCity(filtered, photonRequest.getOrt());

		if (photonRequest.hasPlz())
			filtered = filterPostcode(filtered, photonRequest.getPlz());

		if (photonRequest.hasCountry())
			filtered = filterCountry(filtered, photonRequest.getCountry());

		return filtered;
	}

	@Override
	public TagFilterQueryBuilder buildQuery(SteileRequest photonRequest) {
		TagFilterQueryBuilder builder;

		if (photonRequest.hasOrt() && photonRequest.hasPlz()) { //liste aller Stadtteile für PLZ-Ort-Kombi
			builder = PhotonQueryBuilder.builder(photonRequest.getOrt()+","+photonRequest.hasPlz(), photonRequest.getLanguage());
		}
		else if (photonRequest.hasOrt()) { //liste aller Stadtteile für Ort
			builder = PhotonQueryBuilder.builder(photonRequest.getOrt(), photonRequest.getLanguage());
		}
		else { //liste aller Stadtteile für PLZ
			builder = PhotonQueryBuilder.builder(photonRequest.getPlz(), photonRequest.getLanguage());
		}

		builder.withValues("suburb");

		return builder.withLocationBias(photonRequest.getLocationForBias());
	}
}
