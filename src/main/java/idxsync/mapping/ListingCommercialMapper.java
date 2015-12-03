package idxsync.mapping;

import idxsync.AppUtils;
import idxsync.domain.ListingCommercial;
import org.realtors.rets.client.SearchResult;

import java.util.List;
import java.util.Set;

public class ListingCommercialMapper extends AbstractMapper implements Mapper {

    public ListingCommercialMapper(String mappingFile) {
        super(mappingFile, ListingCommercial.class.getName());
    }

    @Override
    public Set<ListingCommercial> mapData(List<SearchResult> sourceList) {

        return AppUtils.castCollectionSet(mapDomainData(sourceList), ListingCommercial.class);
    }

    @Override
    public Set<ListingCommercial> mapData(SearchResult source) {

        return AppUtils.castCollectionSet(mapDomainData(source), ListingCommercial.class);
    }
}
