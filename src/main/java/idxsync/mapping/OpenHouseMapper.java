package idxsync.mapping;

import idxsync.AppUtils;
import idxsync.domain.OpenHouse;
import org.realtors.rets.client.SearchResult;

import java.util.List;
import java.util.Set;

public class OpenHouseMapper extends AbstractMapper implements Mapper {

    public OpenHouseMapper(String mappingFile) {
        super(mappingFile, OpenHouse.class.getName());
    }

    @Override
    public Set<OpenHouse> mapData(List<SearchResult> sourceList) {

        return AppUtils.castCollectionSet(mapDomainData(sourceList), OpenHouse.class);
    }

    @Override
    public Set<OpenHouse> mapData(SearchResult source) {

        return AppUtils.castCollectionSet(mapDomainData(source), OpenHouse.class);
    }
}
