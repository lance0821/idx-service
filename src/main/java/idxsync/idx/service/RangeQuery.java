package idxsync.idx.service;

public class RangeQuery {
    private String field;
    private Integer from;
    private Integer to;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public static class RangeQueryBuilder {
        private String field;
        private Integer from;
        private Integer to;

        public RangeQueryBuilder setField(String field) {
            this.field = field;

            return this;
        }

        public RangeQueryBuilder setFrom(Integer from) {
            this.from = from;

            return this;
        }

        public RangeQueryBuilder setTo(Integer to) {
            this.to = to;

            return this;
        }

        public RangeQuery build() {
            RangeQuery rangeQuery = new RangeQuery();

            rangeQuery.setField(field);
            rangeQuery.setFrom(from);
            rangeQuery.setTo(to);

            return rangeQuery;
        }
    }
}
