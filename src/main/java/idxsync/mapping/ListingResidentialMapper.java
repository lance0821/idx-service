package idxsync.mapping;


import idxsync.AppUtils;
import idxsync.domain.ListingResidential;
import org.realtors.rets.client.SearchResult;

import java.util.List;
import java.util.Set;

public class ListingResidentialMapper extends AbstractMapper implements Mapper {

    public ListingResidentialMapper(String mappingFile) {
        super(mappingFile, ListingResidential.class.getName());
    }

    @Override
    public Set<ListingResidential> mapData(List<SearchResult> sourceList) {

        return AppUtils.castCollectionSet(mapDomainData(sourceList), ListingResidential.class);
    }

    @Override
    public Set<ListingResidential> mapData(SearchResult source) {

        return AppUtils.castCollectionSet(mapDomainData(source), ListingResidential.class);
    }
}
