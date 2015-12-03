package idxsync.domain;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.Date;

import static org.springframework.data.elasticsearch.annotations.FieldIndex.analyzed;
import static org.springframework.data.elasticsearch.annotations.FieldIndex.not_analyzed;

@Document(indexName = "idx", type = "residential")
public final class ListingResidential extends IdxDomain {

    
    private Date matrixModifiedDate;

    
    private long matrixUniqueId;

    
    @Field(index = not_analyzed) private String streetDirPrefix;

    
    @Field(index = not_analyzed) private String streetName;

    
    private int tmkZone;

    
    @Field(index = not_analyzed) private String associationCommunityName;

    
    @Field(index = not_analyzed) private String propertyType;

    private double listPrice;

    
    @Field(index = not_analyzed) private String streetSuffix;

    
    @Field(index = not_analyzed) private String landTenure;

    
    @Field(index = not_analyzed) private String listingService;

    
    private int sqftGarageCarport;

    
    @Field(index = not_analyzed) private String listAgentEmail;

    
    @Field(index = not_analyzed) private String listOfficePhone;

    
    private int sqftBuilding;

    
    @Field(index = analyzed) private String utilities;

    
    @Field(index = analyzed) private String topography;

    
    private int tmkPlat;

    
    @Field(index = analyzed) private String flooring;

    
    private int photoCount;

    
    @Field(index = not_analyzed) private String streetNumber;

    
    private int tmkCondoPropertyRegimeNumber;

    
    @Field(index = not_analyzed) private String restrictions;

    
    private int yearBuilt;

    
    @Field(index = not_analyzed) private String status;

    
    @Field(index = not_analyzed) private String stateOrProvince;

    
    private int tmkParcel;

    
    @Field(index = not_analyzed) private String colistOfficeMlsId;

    
    private int bathsHalf;

    
    @Field(index = not_analyzed) private String city;

    
    @Field(index = not_analyzed) private String mlsNumber;

    
    @Field(index = not_analyzed) private String colistAgentMlsId;

    
    @Field(index = not_analyzed) private String storiesType;

    
    @Field(index = not_analyzed) private String middleOrJuniorSchool;

    
    @Field(index = not_analyzed) private String floodZone;

    
    private int sqftRoofedLiving;

    
    @Field(index = not_analyzed) private String virtualTourUrlUnbranded;

    
    @Field(index = not_analyzed) private String elementarySchool;

    
    private Date photoModificationTimestamp;

    
    @Field(index = not_analyzed) private String unitNumber;

    
    @Field(index = analyzed) private String publicRemarks;

    private boolean permitAddressInternetYn;

    
    @Field(index = analyzed) private String easements;

    
    @Field(index = not_analyzed) private String streetDirSuffix;

    
    @Field(index = analyzed) private String amenities;

    
    private int floorNumber;

    
    @Field(index = not_analyzed) private String buildingName;

    
    @Field(index = not_analyzed) private String listAgentFullName;

    
    @Field(index = analyzed) private String lotFeatures;

    
    private int bedsTotal;

    
    @Field(index = not_analyzed) private String postalCode;

    
    private int sqftLanaiOpen;

    
    @Field(index = analyzed) private String associationFee2Includes;

    
    @Field(index = analyzed) private String furnished;

    
    @Field(index = analyzed) private String inclusions;

    
    private int sqftTotal;

    
    @Field(index = analyzed) private String constructionMaterials;

    
    private int sqftLanaiCovered;

    
    @Field(index = analyzed) private String propertyCondition;

    
    @Field(index = not_analyzed) private String colistAgentFullName;

    private boolean downPaymentResourceYn;

    
    private int numberOfUnitsTotal;

    
    private int parkingTotal;

    
    @Field(index = not_analyzed) private String neighbourhood;

    
    @Field(index = not_analyzed) private String propertyFrontage;

    
    private int bathsFull;

    
    private int associationFee;

    
    @Field(index = not_analyzed) private String colistOfficeName;

    
    private int tmkSection;

    
    @Field(index = not_analyzed) private String listAgentMlsId;

    
    private int sqftRoofedOther;

    
    private Date listingContractDate;

    
    @Field(index = analyzed) private String unitFeatures;

    
    @Field(index = analyzed) private String architecturalStyle;

    
    @Field(index = analyzed) private String parkingFeatures;

    
    @Field(index = not_analyzed) private String postalCodePlus4;

    
    @Field(index = analyzed) private String view;

    
    @Field(index = analyzed) private String propertySubType;

    private double lotSizeArea;

    
    @Field(index = analyzed) private String poolFeatures;

    private double maintenanceExpense;

    
    @Field(index = not_analyzed) private String highSchool;

    
    @Field(index = not_analyzed) private String listAgentDirectWorkPhone;

    
    @Field(index = not_analyzed) private String mlsAreaMajor;

    
    @Field(index = not_analyzed) private String listOfficeName;

    
    @Field(index = not_analyzed) private String listOfficeMlsId;

    
    private int tmkDivision;

    
    private int yearRemodeled;

    public String getStreetDirPrefix() {
      return streetDirPrefix;
    }

    public void setStreetDirPrefix(String streetDirPrefix) {
      this.streetDirPrefix = streetDirPrefix;
    }

    public String getStreetName() {
      return streetName;
    }

    public void setStreetName(String streetName) {
      this.streetName = streetName;
    }

    public int getTMKZone() {
      return tmkZone;
    }

    public void setTMKZone(int tmkZone) {
      this.tmkZone = tmkZone;
    }

    public String getAssociationCommunityName() {
      return associationCommunityName;
    }

    public void setAssociationCommunityName(String associationCommunityName) {
      this.associationCommunityName = associationCommunityName;
    }

    public String getPropertyType() {
      return propertyType;
    }

    public void setPropertyType(String propertyType) {
      this.propertyType = propertyType;
    }

    public double getListPrice() {
      return listPrice;
    }

    public void setListPrice(double listPrice) {
      this.listPrice = listPrice;
    }

    public String getStreetSuffix() {
      return streetSuffix;
    }

    public void setStreetSuffix(String streetSuffix) {
      this.streetSuffix = streetSuffix;
    }

    public String getLandTenure() {
      return landTenure;
    }

    public void setLandTenure(String landTenure) {
      this.landTenure = landTenure;
    }

    public String getListingService() {
      return listingService;
    }

    public void setListingService(String listingService) {
      this.listingService = listingService;
    }

    public int getSQFTGarageCarport() {
      return sqftGarageCarport;
    }

    public void setSQFTGarageCarport(int sqftGarageCarport) {
      this.sqftGarageCarport = sqftGarageCarport;
    }

    public String getListAgentEmail() {
      return listAgentEmail;
    }

    public void setListAgentEmail(String listAgentEmail) {
      this.listAgentEmail = listAgentEmail;
    }

    public String getListOfficePhone() {
      return listOfficePhone;
    }

    public void setListOfficePhone(String listOfficePhone) {
      this.listOfficePhone = listOfficePhone;
    }

    public int getSQFTBuilding() {
      return sqftBuilding;
    }

    public void setSQFTBuilding(int sqftBuilding) {
      this.sqftBuilding = sqftBuilding;
    }

    public String getUtilities() {
      return utilities;
    }

    public void setUtilities(String utilities) {
      this.utilities = utilities;
    }

    public String getTopography() {
      return topography;
    }

    public void setTopography(String topography) {
      this.topography = topography;
    }

    public int getTmkPlat() {
      return tmkPlat;
    }

    public void setTmkPlat(int tmkPlat) {
      this.tmkPlat = tmkPlat;
    }

    public String getFlooring() {
      return flooring;
    }

    public void setFlooring(String flooring) {
      this.flooring = flooring;
    }

    public int getPhotoCount() {
      return photoCount;
    }

    public void setPhotoCount(int photoCount) {
      this.photoCount = photoCount;
    }

    public String getStreetNumber() {
      return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
      this.streetNumber = streetNumber;
    }

    public int getTmkCondoPropertyRegimeNumber() {
      return tmkCondoPropertyRegimeNumber;
    }

    public void setTmkCondoPropertyRegimeNumber(int tmkCondoPropertyRegimeNumber) {
      this.tmkCondoPropertyRegimeNumber = tmkCondoPropertyRegimeNumber;
    }

    public String getRestrictions() {
      return restrictions;
    }

    public void setRestrictions(String restrictions) {
      this.restrictions = restrictions;
    }

    public int getYearBuilt() {
      return yearBuilt;
    }

    public void setYearBuilt(int yearBuilt) {
      this.yearBuilt = yearBuilt;
    }

    public String getStatus() {
      return status;
    }

    public void setStatus(String status) {
      this.status = status;
    }

    public String getStateOrProvince() {
      return stateOrProvince;
    }

    public void setStateOrProvince(String stateOrProvince) {
      this.stateOrProvince = stateOrProvince;
    }

    public int getTmkParcel() {
      return tmkParcel;
    }

    public void setTmkParcel(int tmkParcel) {
      this.tmkParcel = tmkParcel;
    }

    public String getCoListOfficeMlsId() {
      return colistOfficeMlsId;
    }

    public void setCoListOfficeMlsId(String colistOfficeMlsId) {
      this.colistOfficeMlsId = colistOfficeMlsId;
    }

    public int getBathsHalf() {
      return bathsHalf;
    }

    public void setBathsHalf(int bathsHalf) {
      this.bathsHalf = bathsHalf;
    }

    public String getCity() {
      return city;
    }

    public void setCity(String city) {
      this.city = city;
    }

    public String getMlsNumber() {
      return mlsNumber;
    }

    public void setMlsNumber(String mlsNumber) {
      this.mlsNumber = mlsNumber;
    }

    public String getCoListAgentMlsId() {
      return colistAgentMlsId;
    }

    public void setCoListAgentMlsId(String colistAgentMlsId) {
      this.colistAgentMlsId = colistAgentMlsId;
    }

    public String getStoriesType() {
      return storiesType;
    }

    public void setStoriesType(String storiesType) {
      this.storiesType = storiesType;
    }

    public String getMiddleOrJuniorSchool() {
      return middleOrJuniorSchool;
    }

    public void setMiddleOrJuniorSchool(String middleOrJuniorSchool) {
      this.middleOrJuniorSchool = middleOrJuniorSchool;
    }

    public String getFloodZone() {
      return floodZone;
    }

    public void setFloodZone(String floodZone) {
      this.floodZone = floodZone;
    }

    public int getSQFTRoofedLiving() {
      return sqftRoofedLiving;
    }

    public void setSQFTRoofedLiving(int sqftRoofedLiving) {
      this.sqftRoofedLiving = sqftRoofedLiving;
    }

    public String getVirtualTourUrlUnbranded() {
      return virtualTourUrlUnbranded;
    }

    public void setVirtualTourUrlUnbranded(String virtualTourUrlUnbranded) {
      this.virtualTourUrlUnbranded = virtualTourUrlUnbranded;
    }

    public String getElementarySchool() {
      return elementarySchool;
    }

    public void setElementarySchool(String elementarySchool) {
      this.elementarySchool = elementarySchool;
    }

    public Date getPhotoModificationTimestamp() {
      return photoModificationTimestamp;
    }

    public void setPhotoModificationTimestamp(Date photoModificationTimestamp) {
      this.photoModificationTimestamp = photoModificationTimestamp;
    }

    public String getUnitNumber() {
      return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
      this.unitNumber = unitNumber;
    }

    public String getPublicRemarks() {
      return publicRemarks;
    }

    public void setPublicRemarks(String publicRemarks) {
      this.publicRemarks = publicRemarks;
    }

    public boolean getPermitAddressInternetYn() {
      return permitAddressInternetYn;
    }

    public void setPermitAddressInternetYn(boolean permitAddressInternetYn) {
      this.permitAddressInternetYn = permitAddressInternetYn;
    }

    public String getEasements() {
      return easements;
    }

    public void setEasements(String easements) {
      this.easements = easements;
    }

    public String getStreetDirSuffix() {
      return streetDirSuffix;
    }

    public void setStreetDirSuffix(String streetDirSuffix) {
      this.streetDirSuffix = streetDirSuffix;
    }

    public String getAmenities() {
      return amenities;
    }

    public void setAmenities(String amenities) {
      this.amenities = amenities;
    }

    public int getFloorNumber() {
      return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
      this.floorNumber = floorNumber;
    }

    public String getBuildingName() {
      return buildingName;
    }

    public void setBuildingName(String buildingName) {
      this.buildingName = buildingName;
    }

    public String getListAgentFullName() {
      return listAgentFullName;
    }

    public void setListAgentFullName(String listAgentFullName) {
      this.listAgentFullName = listAgentFullName;
    }

    public String getLotFeatures() {
      return lotFeatures;
    }

    public void setLotFeatures(String lotFeatures) {
      this.lotFeatures = lotFeatures;
    }

    public int getBedsTotal() {
      return bedsTotal;
    }

    public void setBedsTotal(int bedsTotal) {
      this.bedsTotal = bedsTotal;
    }

    public String getPostalCode() {
      return postalCode;
    }

    public void setPostalCode(String postalCode) {
      this.postalCode = postalCode;
    }

    public int getSQFTLanaiOpen() {
      return sqftLanaiOpen;
    }

    public void setSQFTLanaiOpen(int sqftLanaiOpen) {
      this.sqftLanaiOpen = sqftLanaiOpen;
    }

    public String getAssociationFee2Includes() {
      return associationFee2Includes;
    }

    public void setAssociationFee2Includes(String associationFee2Includes) {
      this.associationFee2Includes = associationFee2Includes;
    }

    public String getFurnished() {
      return furnished;
    }

    public void setFurnished(String furnished) {
      this.furnished = furnished;
    }

    public String getInclusions() {
      return inclusions;
    }

    public void setInclusions(String inclusions) {
      this.inclusions = inclusions;
    }

    public int getSqftTotal() {
      return sqftTotal;
    }

    public void setSqftTotal(int sqftTotal) {
      this.sqftTotal = sqftTotal;
    }

    public String getConstructionMaterials() {
      return constructionMaterials;
    }

    public void setConstructionMaterials(String constructionMaterials) {
      this.constructionMaterials = constructionMaterials;
    }

    public int getSQFTLanaiCovered() {
      return sqftLanaiCovered;
    }

    public void setSQFTLanaiCovered(int sqftLanaiCovered) {
      this.sqftLanaiCovered = sqftLanaiCovered;
    }

    public String getPropertyCondition() {
      return propertyCondition;
    }

    public void setPropertyCondition(String propertyCondition) {
      this.propertyCondition = propertyCondition;
    }

    public String getCoListAgentFullName() {
      return colistAgentFullName;
    }

    public void setCoListAgentFullName(String colistAgentFullName) {
      this.colistAgentFullName = colistAgentFullName;
    }

    public boolean getDownPaymentResourceYn() {
      return downPaymentResourceYn;
    }

    public void setDownPaymentResourceYn(boolean downPaymentResourceYn) {
      this.downPaymentResourceYn = downPaymentResourceYn;
    }

    public int getNumberOfUnitsTotal() {
      return numberOfUnitsTotal;
    }

    public void setNumberOfUnitsTotal(int numberOfUnitsTotal) {
      this.numberOfUnitsTotal = numberOfUnitsTotal;
    }

    public int getParkingTotal() {
      return parkingTotal;
    }

    public void setParkingTotal(int parkingTotal) {
      this.parkingTotal = parkingTotal;
    }

    public String getNeighbourhood() {
      return neighbourhood;
    }

    public void setNeighbourhood(String neighbourhood) {
      this.neighbourhood = neighbourhood;
    }

    public String getPropertyFrontage() {
      return propertyFrontage;
    }

    public void setPropertyFrontage(String propertyFrontage) {
      this.propertyFrontage = propertyFrontage;
    }

    public int getBathsFull() {
      return bathsFull;
    }

    public void setBathsFull(int bathsFull) {
      this.bathsFull = bathsFull;
    }

    public int getAssociationFee() {
      return associationFee;
    }

    public void setAssociationFee(int associationFee) {
      this.associationFee = associationFee;
    }

    public String getCoListOfficeName() {
      return colistOfficeName;
    }

    public void setCoListOfficeName(String colistOfficeName) {
      this.colistOfficeName = colistOfficeName;
    }

    public int getTmkSection() {
      return tmkSection;
    }

    public void setTmkSection(int tmkSection) {
      this.tmkSection = tmkSection;
    }

    public String getListAgentMlsId() {
      return listAgentMlsId;
    }

    public void setListAgentMlsId(String listAgentMlsId) {
      this.listAgentMlsId = listAgentMlsId;
    }

    public int getSQFTRoofedOther() {
      return sqftRoofedOther;
    }

    public void setSQFTRoofedOther(int sqftRoofedOther) {
      this.sqftRoofedOther = sqftRoofedOther;
    }

    public Date getListingContractDate() {
      return listingContractDate;
    }

    public void setListingContractDate(Date listingContractDate) {
      this.listingContractDate = listingContractDate;
    }

    public String getUnitFeatures() {
      return unitFeatures;
    }

    public void setUnitFeatures(String unitFeatures) {
      this.unitFeatures = unitFeatures;
    }

    public String getArchitecturalStyle() {
      return architecturalStyle;
    }

    public void setArchitecturalStyle(String architecturalStyle) {
      this.architecturalStyle = architecturalStyle;
    }

    public String getParkingFeatures() {
      return parkingFeatures;
    }

    public void setParkingFeatures(String parkingFeatures) {
      this.parkingFeatures = parkingFeatures;
    }

    public String getPostalCodePlus4() {
      return postalCodePlus4;
    }

    public void setPostalCodePlus4(String postalCodePlus4) {
      this.postalCodePlus4 = postalCodePlus4;
    }

    public String getView() {
      return view;
    }

    public void setView(String view) {
      this.view = view;
    }

    public String getPropertySubType() {
      return propertySubType;
    }

    public void setPropertySubType(String propertySubType) {
      this.propertySubType = propertySubType;
    }

    public double getLotSizeArea() {
      return lotSizeArea;
    }

    public void setLotSizeArea(double lotSizeArea) {
      this.lotSizeArea = lotSizeArea;
    }

    public String getPoolFeatures() {
      return poolFeatures;
    }

    public void setPoolFeatures(String poolFeatures) {
      this.poolFeatures = poolFeatures;
    }

    public double getMaintenanceExpense() {
      return maintenanceExpense;
    }

    public void setMaintenanceExpense(double maintenanceExpense) {
      this.maintenanceExpense = maintenanceExpense;
    }

    public String getHighSchool() {
      return highSchool;
    }

    public void setHighSchool(String highSchool) {
      this.highSchool = highSchool;
    }

    public String getListAgentDirectWorkPhone() {
      return listAgentDirectWorkPhone;
    }

    public void setListAgentDirectWorkPhone(String listAgentDirectWorkPhone) {
      this.listAgentDirectWorkPhone = listAgentDirectWorkPhone;
    }

    public String getMlsAreaMajor() {
      return mlsAreaMajor;
    }

    public void setMlsAreaMajor(String mlsAreaMajor) {
      this.mlsAreaMajor = mlsAreaMajor;
    }

    public String getListOfficeName() {
      return listOfficeName;
    }

    public void setListOfficeName(String listOfficeName) {
      this.listOfficeName = listOfficeName;
    }

    public String getListOfficeMlsId() {
      return listOfficeMlsId;
    }

    public void setListOfficeMlsId(String listOfficeMlsId) {
      this.listOfficeMlsId = listOfficeMlsId;
    }

    public int getTmkDivision() {
      return tmkDivision;
    }

    public void setTmkDivision(int tmkDivision) {
      this.tmkDivision = tmkDivision;
    }

    public int getYearRemodeled() {
      return yearRemodeled;
    }

    public void setYearRemodeled(int yearRemodeled) {
      this.yearRemodeled = yearRemodeled;
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
}
