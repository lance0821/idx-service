package idxsync.persistence.repository;

import idxsync.domain.SyncStat;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

import java.util.List;

public interface SyncStatRepository extends ElasticsearchCrudRepository<SyncStat, String> {

    List<SyncStat> findAllBySyncTypeOrderByUpdatedDesc(String syncType);
}
