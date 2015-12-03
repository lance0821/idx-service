package idxsync.sync.strategy;

import idxsync.mapping.Mapper;
import idxsync.rets.RetsConnection;
import org.springframework.data.repository.CrudRepository;

public class SyncStrategyConfig {

    private String domainName;
    private String syncQuery;
    private String deleteQuery;
    private RetsConnection retsConnection;
    private Mapper mapper;
    private CrudRepository domainRepository;

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getSyncQuery() {
        return syncQuery;
    }

    public void setSyncQuery(String syncQuery) {
        this.syncQuery = syncQuery;
    }

    public String getDeleteQuery() {
        return deleteQuery;
    }

    public void setDeleteQuery(String deleteQuery) {
        this.deleteQuery = deleteQuery;
    }

    public RetsConnection getRetsConnection() {
        return retsConnection;
    }

    public void setRetsConnection(RetsConnection retsConnection) {
        this.retsConnection = retsConnection;
    }

    public Mapper getMapper() {
        return mapper;
    }

    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    public CrudRepository getDomainRepository() {
        return domainRepository;
    }

    public void setDomainRepository(CrudRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    /**
     * Static SyncStrategyConfig builder class
     */
    public static class SyncConfigBuilder {

        private String domainName;
        private String syncQuery;
        private String deleteQuery;
        private RetsConnection retsConnection;
        private Mapper mapper;
        private CrudRepository domainRepository;

        public SyncConfigBuilder setDomainName(String domainName) {
            this.domainName = domainName;

            return this;
        }

        public SyncConfigBuilder setSyncQuery(String syncQuery) {
            this.syncQuery = syncQuery;

            return this;
        }

        public SyncConfigBuilder setDeleteQuery(String deleteQuery) {
            this.deleteQuery = deleteQuery;

            return this;
        }

        public SyncConfigBuilder setRetsConnection(RetsConnection retsConnection) {
            this.retsConnection = retsConnection;

            return this;
        }

        public SyncConfigBuilder setMapper(Mapper mapper) {
            this.mapper = mapper;

            return this;
        }

        public SyncConfigBuilder setDomainRepository(CrudRepository domainRepository) {
            this.domainRepository = domainRepository;

            return this;
        }

        public SyncStrategyConfig build() {
            SyncStrategyConfig syncStrategyConfig = new SyncStrategyConfig();
            syncStrategyConfig.setDomainName(domainName);
            syncStrategyConfig.setSyncQuery(syncQuery);
            syncStrategyConfig.setDeleteQuery(deleteQuery);
            syncStrategyConfig.setRetsConnection(retsConnection);
            syncStrategyConfig.setMapper(mapper);
            syncStrategyConfig.setDomainRepository(domainRepository);

            return syncStrategyConfig;
        }
    }

}