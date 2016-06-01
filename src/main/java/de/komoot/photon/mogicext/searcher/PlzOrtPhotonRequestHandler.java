package de.komoot.photon.mogicext.searcher;

import com.vividsolutions.jts.geom.Point;
import de.komoot.photon.mogicext.PlzOrtRequest;
import de.komoot.photon.query.PhotonQueryBuilder;
import de.komoot.photon.query.TagFilterQueryBuilder;
import de.komoot.photon.searcher.AbstractPhotonRequestHandler;
import de.komoot.photon.searcher.ElasticsearchSearcher;
import de.komoot.photon.searcher.PhotonRequestHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlzOrtPhotonRequestHandler extends AbstractPhotonRequestHandler<PlzOrtRequest> implements PhotonRequestHandler<PlzOrtRequest> {
	public PlzOrtPhotonRequestHandler(ElasticsearchSearcher elasticsearchSearcher) {
		super(elasticsearchSearcher);
	}

	@Override
	public TagFilterQueryBuilder buildQuery(PlzOrtRequest photonRequest) {
		Point point = photonRequest.getLocationForBias();

		Set<String> values = new HashSet<>(1);
		values.add("city");
		Map<String,Set<String>> tags = new HashMap<>();
		tags.put("place", values);

		//TODO plz/ort richtig nutzen
		return PhotonQueryBuilder.builder(photonRequest.getQuery(), photonRequest.getLanguage())
				.withTags(tags)
				.withLocationBias(point);
	}
}
