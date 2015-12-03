package idxsync.persistence.repository;

import idxsync.domain.ListingResidential;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

public interface ListingResidentialRepository extends ElasticsearchCrudRepository<ListingResidential, String> {

    Iterable<ListingResidential> findAllByMatrixModifiedDateGreaterThanEqual(Long matrixModifiedDate);
    Iterable<ListingResidential> findAllByPhotoModificationTimestampGreaterThanEqualAndPhotoCountGreaterThan(Long timestamp, int photoCount);

    ListingResidential findByMatrixUniqueId(long matrixUniqueId);
    ListingResidential findByMlsNumber(String mlsNumber);

}