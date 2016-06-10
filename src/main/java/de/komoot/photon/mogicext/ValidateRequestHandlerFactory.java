package de.komoot.photon.mogicext;

import de.komoot.photon.query.PhotonRequest;
import de.komoot.photon.searcher.ElasticsearchSearcher;
import de.komoot.photon.searcher.PhotonRequestHandler;
import de.komoot.photon.mogicext.searcher.ValidatePhotonRequestHandler;

public class ValidateRequestHandlerFactory {

    private final ElasticsearchSearcher elasticsearchSearcher;

    public ValidateRequestHandlerFactory(ElasticsearchSearcher elasticsearchSearcher) {
        this.elasticsearchSearcher = elasticsearchSearcher;
    }

    /**
     * Given a {@link ValidateRequest} create a
     * {@link ValidatePhotonRequestHandler handler} that can execute the elastic search
     * search.
     */
    public <R extends PhotonRequest> PhotonRequestHandler<R> createHandler(R request) {
        return (PhotonRequestHandler<R>) new ValidatePhotonRequestHandler(elasticsearchSearcher);
    }
}
