package idxsync.domain;

import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "idx", type = "search_term_school")
public class SearchTermSchool extends SearchTerm {
}
