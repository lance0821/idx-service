package idxsync.domain;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.Date;

import static org.springframework.data.elasticsearch.annotations.FieldIndex.analyzed;
import static org.springframework.data.elasticsearch.annotations.FieldIndex.not_analyzed;

@Document(indexName = "idx", type = "openhouse")
public final class OpenHouse extends IdxDomain {

    
    private Date matrixModifiedDate;

    
    private long matrixUniqueId;

    
    @Field(index = analyzed) private String refreshments;

    
    @Field(index = analyzed) private String description;

    
    private long listingMatrixUniqueId;

    
    private int endTime;

    
    @Field(index = not_analyzed) private String mlsNumber;

    
    private int entryOrder;

    
    private int startTime;

    
    private Date openHouseDate;

    private boolean activeYn;

    
    @Field(index = not_analyzed) private String openHouseType;

    public String getRefreshments() {
      return refreshments;
    }

    public void setRefreshments(String refreshments) {
      this.refreshments = refreshments;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public long getListingMatrixUniqueId() {
      return listingMatrixUniqueId;
    }

    public void setListingMatrixUniqueId(long listingMatrixUniqueId) {
      this.listingMatrixUniqueId = listingMatrixUniqueId;
    }

    public int getEndTime() {
      return endTime;
    }

    public void setEndTime(int endTime) {
      this.endTime = endTime;
    }

    public String getMlsNumber() {
      return mlsNumber;
    }

    public void setMlsNumber(String mlsNumber) {
      this.mlsNumber = mlsNumber;
    }

    public int getEntryOrder() {
      return entryOrder;
    }

    public void setEntryOrder(int entryOrder) {
      this.entryOrder = entryOrder;
    }

    public int getStartTime() {
      return startTime;
    }

    public void setStartTime(int startTime) {
      this.startTime = startTime;
    }

    public Date getOpenHouseDate() {
      return openHouseDate;
    }

    public void setOpenHouseDate(Date openHouseDate) {
      this.openHouseDate = openHouseDate;
    }

    public boolean getActiveYn() {
      return activeYn;
    }

    public void setActiveYn(boolean activeYN) {
      this.activeYn = activeYN;
    }

    public String getOpenHouseType() {
      return openHouseType;
    }

    public void setOpenHouseType(String openHouseType) {
      this.openHouseType = openHouseType;
    }

    @Override
    public Date getMatrixModifiedDate() {
        return matrixModifiedDate;
    }

    @Override
    public void setMatrixModifiedDate(Date matrixModifiedDate) {
        this.matrixModifiedDate = matrixModifiedDate;
    }

    @Override
    public long getMatrixUniqueId() {
        return matrixUniqueId;
    }

    @Override
    public void setMatrixUniqueId(long matrixUniqueId) {
        this.matrixUniqueId = matrixUniqueId;
    }

    @Override
    public int getPhotoCount() {
        return 0;
    }

    @Override
    public void setPhotoCount(int photoCount) {

    }

    @Override
    public Date getPhotoModificationTimestamp() {
        return null;
    }

    @Override
    public void setPhotoModificationTimestamp(Date photoModificationTimestamp) {

    }
}
