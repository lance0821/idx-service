package idxsync.idx.service;

public class PageConfig {

    private Integer page;
    private Integer size;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public static class PageConfigBuilder {
        private Integer page;
        private Integer size;

        public PageConfigBuilder setPage(Integer page) {
            this.page = page;

            return this;
        }

        public PageConfigBuilder setSize(Integer size) {
            this.size = size;

            return this;
        }

        public PageConfig build() {
            PageConfig pageConfig = new PageConfig();
            pageConfig.setPage(page);
            pageConfig.setSize(size);

            return pageConfig;
        }
    }
}
