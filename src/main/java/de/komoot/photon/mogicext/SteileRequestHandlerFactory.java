package de.komoot.photon.mogicext;

import de.komoot.photon.mogicext.searcher.SteilePhotonRequestHandler;
import de.komoot.photon.query.PhotonRequest;
import de.komoot.photon.searcher.ElasticsearchSearcher;
import de.komoot.photon.searcher.PhotonRequestHandler;

public class SteileRequestHandlerFactory {

    private final ElasticsearchSearcher elasticsearchSearcher;

    public SteileRequestHandlerFactory(ElasticsearchSearcher elasticsearchSearcher) {
        this.elasticsearchSearcher = elasticsearchSearcher;
    }

    /**
     * Given a {@link SteileRequest} create a
     * {@link SteilePhotonRequestHandler handler} that can execute the elastic search
     * search.
     */
    public <R extends PhotonRequest> PhotonRequestHandler<R> createHandler(R request) {
        return (PhotonRequestHandler<R>) new SteilePhotonRequestHandler(elasticsearchSearcher);
    }
}
