package de.komoot.photon.mogicext.searcher;

import de.komoot.photon.mogicext.SteileRequest;
import de.komoot.photon.query.PhotonQueryBuilder;
import de.komoot.photon.query.TagFilterQueryBuilder;
import de.komoot.photon.searcher.AbstractPhotonRequestHandler;
import de.komoot.photon.searcher.ElasticsearchSearcher;
import de.komoot.photon.searcher.PhotonRequestHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SteilePhotonRequestHandler extends AbstractPhotonRequestHandler<SteileRequest> implements PhotonRequestHandler<SteileRequest> {
	public SteilePhotonRequestHandler(ElasticsearchSearcher elasticsearchSearcher) {
		super(elasticsearchSearcher);
	}

	@Override
	public TagFilterQueryBuilder buildQuery(SteileRequest photonRequest) {
		TagFilterQueryBuilder builder;

		//XXX nur ein dummy
		builder = PhotonQueryBuilder.builder(photonRequest.getOrt(), photonRequest.getLanguage());

		Set<String> values = new HashSet<>(1);
		values.add("city");
		Map<String,Set<String>> tags = new HashMap<>();
		tags.put("place", values);
		builder = builder.withTags(tags);

		//TODO kopiert, muss noch angepasst werden
		if (photonRequest.hasOrt() && photonRequest.hasPlz()) {
			//TODO liste aller Stadtteile für PLZ-Ort-Kombi
		}
		if (photonRequest.hasOrt()) {
			//TODO liste aller Stadtteile für Ort
		}
		else {
			//TODO liste aller Stadtteile für PLZ
		}

		return builder;

		//TODO nur, wenn gesetzt
//		return builder.withLocationBias(photonRequest.getLocationForBias());
	}
}
