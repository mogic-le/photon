package de.komoot.photon.mogicext;

import de.komoot.photon.mogicext.searcher.PlzOrtPhotonRequestHandler;
import de.komoot.photon.query.PhotonRequest;
import de.komoot.photon.searcher.ElasticsearchSearcher;
import de.komoot.photon.searcher.PhotonRequestHandler;

public class PlzOrtRequestHandlerFactory {

    private final ElasticsearchSearcher elasticsearchSearcher;

    public PlzOrtRequestHandlerFactory(ElasticsearchSearcher elasticsearchSearcher) {
        this.elasticsearchSearcher = elasticsearchSearcher;
    }

    /**
     * Given a {@link PlzOrtRequest} create a
     * {@link PlzOrtSearchRequestHandler handler} that can execute the elastic search
     * search.
     */
    public <R extends PhotonRequest> PhotonRequestHandler<R> createHandler(R request) {
        return (PhotonRequestHandler<R>) new PlzOrtPhotonRequestHandler(elasticsearchSearcher);
    }
}
