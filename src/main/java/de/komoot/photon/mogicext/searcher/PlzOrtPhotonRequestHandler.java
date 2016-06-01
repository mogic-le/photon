package de.komoot.photon.mogicext.searcher;

import com.vividsolutions.jts.geom.Point;
import de.komoot.photon.mogicext.PlzOrtRequest;
import de.komoot.photon.query.PhotonQueryBuilder;
import de.komoot.photon.query.TagFilterQueryBuilder;
import de.komoot.photon.searcher.AbstractPhotonRequestHandler;
import de.komoot.photon.searcher.ElasticsearchSearcher;
import de.komoot.photon.searcher.PhotonRequestHandler;

public class PlzOrtPhotonRequestHandler extends AbstractPhotonRequestHandler<PlzOrtRequest> implements PhotonRequestHandler<PlzOrtRequest> {
	public PlzOrtPhotonRequestHandler(ElasticsearchSearcher elasticsearchSearcher) {
		super(elasticsearchSearcher);
	}

	@Override
	public TagFilterQueryBuilder buildQuery(PlzOrtRequest photonRequest) {
		Point point = photonRequest.getLocationForBias();
		//TODO plz/ort richtig nutzen
		return PhotonQueryBuilder.builder(photonRequest.getQuery(), photonRequest.getLanguage()).withLocationBias(point);
	}
}
