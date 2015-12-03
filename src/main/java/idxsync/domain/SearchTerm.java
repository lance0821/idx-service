package idxsync.domain;

import org.springframework.data.elasticsearch.annotations.Field;

import java.util.UUID;

import static org.springframework.data.elasticsearch.annotations.FieldIndex.not_analyzed;

public abstract class SearchTerm implements Comparable<SearchTerm> {

    private String id;

    public SearchTerm() {
        id = UUID.randomUUID().toString();
    }

    @Field(index = not_analyzed) private String searchTerm;
    @Field(index = not_analyzed) private SearchTermType type;

    public String getId() {
        return id;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public SearchTermType getType() {
        return type;
    }

    public void setType(SearchTermType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this.searchTerm == null || this.type == null) return false;

        return this.searchTerm.toLowerCase().equals(((SearchTermResidential) o).getSearchTerm().toLowerCase()) &&
                this.type.equals(((SearchTermResidential) o).getType());
    }

    @Override
    public int compareTo(SearchTerm o) {
        if (this.searchTerm == null || this.type == null) return -1;

        if (this.type.getType().equals(o.getType().getType()))
            return this.searchTerm.toLowerCase().compareTo(o.getSearchTerm().toLowerCase());
        else
            return this.type.getType().compareTo(o.getType().getType());
    }
}
