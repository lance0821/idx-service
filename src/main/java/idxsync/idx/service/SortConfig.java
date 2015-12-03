package idxsync.idx.service;

import org.elasticsearch.search.sort.SortOrder;

public class SortConfig {

    private String field;
    private SortOrder order;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public SortOrder getOrder() {
        return order;
    }

    public void setOrder(SortOrder order) {
        this.order = order;
    }

    public static class SortConfigBuilder {
        private String field;
        private SortOrder order;

        public SortConfigBuilder setField(String field) {
            this.field = field;

            return this;
        }

        public SortConfigBuilder setOrder(String order) {
            if (SortOrder.ASC.toString().equalsIgnoreCase(order)) {
                this.order = SortOrder.ASC;
            }
            else this.order = SortOrder.DESC;

            return this;
        }

        public SortConfig build() {
            SortConfig sortConfig = new SortConfig();
            sortConfig.setField(field);
            sortConfig.setOrder(order);

            return sortConfig;
        }
    }
}
