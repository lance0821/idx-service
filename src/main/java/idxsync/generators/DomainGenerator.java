package idxsync.generators;


import idxsync.rets.metadata.Metadata;
import idxsync.rets.metadata.Table;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DomainGenerator {

    public static void generateDomainClassFiles(Metadata metadata) throws IOException {
        String listingResidential = DomainGenerator.generateDomainClass(
                "idxsync.domain",
                "ListingResidential",
                "idx_residential_listings",
                "Property",
                "RESI",
                metadata);

        String listingCommercial = DomainGenerator.generateDomainClass(
                "idxsync.domain",
                "ListingCommercial",
                "idx_commercial_listings",
                "Property",
                "COMM",
                metadata);

        String listingMult = DomainGenerator.generateDomainClass(
                "idxsync.domain",
                "ListingMult",
                "idx_mult_listings",
                "Property",
                "MULT",
                metadata);

        String listingLand = DomainGenerator.generateDomainClass(
                "idxsync.domain",
                "ListingLand",
                "idx_land_listings",
                "Property",
                "LAND",
                metadata);

        String openHouse = DomainGenerator.generateDomainClass(
                "idxsync.domain",
                "OpenHouse",
                "idx_openhouse",
                "OpenHouse",
                "OPENHOUSE",
                metadata);

        String media = DomainGenerator.generateDomainClass(
                "idxsync.domain",
                "Image",
                "idx_media",
                "Image",
                "MEDIA",
                metadata);

        Files.write(Paths.get("./src/main/java/idxsync/domain/ListingResidential.java"), listingResidential.getBytes());
        Files.write(Paths.get("./src/main/java/idxsync/domain/ListingCommercial.java"), listingCommercial.getBytes());
        Files.write(Paths.get("./src/main/java/idxsync/domain/ListingMult.java"), listingMult.getBytes());
        Files.write(Paths.get("./src/main/java/idxsync/domain/ListingLand.java"), listingLand.getBytes());
        Files.write(Paths.get("./src/main/java/idxsync/domain/OpenHouse.java"), openHouse.getBytes());
        Files.write(Paths.get("./src/main/java/idxsync/domain/Image.java"), media.getBytes());

        String listingResiRepo = generateDomainRepository("idxsync.persistence.repository", "ListingResidentialRepository", "ListingResidential");
        String listingCommRepo = generateDomainRepository("idxsync.persistence.repository", "ListingCommercialRepository", "ListingCommerical");
        String listingMultRepo = generateDomainRepository("idxsync.persistence.repository", "ListingMultRepository", "ListingMult");
        String listingLandRepo = generateDomainRepository("idxsync.persistence.repository", "ListingLandRepository", "ListingLand");
        String openHouseRepo = generateDomainRepository("idxsync.persistence.repository", "OpenHouseRepository", "OpenHouse");
        String mediaRepo = generateDomainRepository("idxsync.persistence.repository", "MediaRepository", "Image");

        Files.write(Paths.get("./src/main/java/idxsync/persistence/repository/ListingResidentialRepository.java"), listingResiRepo.getBytes());
        Files.write(Paths.get("./src/main/java/idxsync/persistence/repository/ListingCommercialRepository.java"), listingCommRepo.getBytes());
        Files.write(Paths.get("./src/main/java/idxsync/persistence/repository/ListingMultRepository.java"), listingMultRepo.getBytes());
        Files.write(Paths.get("./src/main/java/idxsync/persistence/repository/ListingLandRepository.java"), listingLandRepo.getBytes());
        Files.write(Paths.get("./src/main/java/idxsync/persistence/repository/OpenHouseRepository.java"), openHouseRepo.getBytes());
        Files.write(Paths.get("./src/main/java/idxsync/persistence/repository/MediaRepository.java"), mediaRepo.getBytes());
    }

    public static String generateDomainRepository(String packageName, String repoName, String domainClass) {

        StringBuilder repoSB = new StringBuilder();

        repoSB.append(String.format("package %s;\n\n", packageName) +
                String.format("import idxsync.domain.%s;\n\n", domainClass) +
                String.format("public interface %s extends BaseRepository<%s, Long> {\n\n", repoName, domainClass) +
                "}");

        return repoSB.toString();
    }

    public static String generateDomainClass(String packageName, String domainClazzName, String dbTableName,
                                             String resourceId, String classificationName, Metadata metadata) {

        StringBuilder clazzSB = new StringBuilder();
        clazzSB.append(String.format("package %s;\n\n", packageName) +
                "import javax.persistence.Column;\n" +
                "import javax.persistence.Entity;\n" +
                "import javax.persistence.Table;\n" +
                "import java.util.Date;\n\n\n");
        clazzSB.append("@Entity\n" +
                String.format("@Table(name = \"%s\")\n", dbTableName) +
                String.format("public final class %s extends IdxDomain {\n\n", domainClazzName));

        Map<String, String> codeMap = buildInstanceVarsWithAccessors(metadata, resourceId, classificationName);
        clazzSB.append(codeMap.get("vars"));
        clazzSB.append(codeMap.get("methods"));

        clazzSB.append("}\n");

        return clazzSB.toString();
    }

    protected static Map<String, String> buildInstanceVarsWithAccessors(Metadata metadata, String resourceId, String classId) {
        List<Table> tableList =
                metadata.getResources().get(resourceId).getClassifications().get(classId).getTableList();

        StringBuilder varsSB = new StringBuilder();
        StringBuilder methodsSB = new StringBuilder();

        tableList.forEach(table -> {
            String varName = toCamelCase(table.getLongName());

            String dataType = "";

            switch (table.getDataType()) {
                case "Character":

                    if (table.getMaxLength() > -1) {
                        varsSB.append(String.format("\t\t@Column(length=%d)\n", table.getMaxLength()));
                    }

                    dataType = "String";

                    break;
                case "Tiny":
                case "Small":
                case "Int":
                case "Long":
                case "Decimal":
                    if (table.getMaxLength() > -1 || table.getPrecision() > -1) {

                        varsSB.append("\t\t@Column(");

                        boolean hasMaxLength = false;

                        if (table.getMaxLength() > 0) {
                            varsSB.append(String.format("precision=%d", table.getMaxLength()));
                            hasMaxLength = true;
                        }

                        if (table.getPrecision() > 0) {
                            if (hasMaxLength) varsSB.append(", ");
                            varsSB.append(String.format("scale=%d", table.getPrecision()));
                        }

                        varsSB.append(")\n");
                    }

                    if ("Tiny".equals(table.getDataType()) ||
                            "Small".equals(table.getDataType())) {
                        dataType = "short";
                    } else if ("Long".equals(table.getDataType())) {
                        dataType = "long";
                    } else if ("Decimal".equals(table.getDataType())) {
                        dataType = "double";
                    } else {
                        dataType = "int";
                    }

                    break;
                case "Date":
                case "DateTime":
                case "Time":
                    dataType = "ZonedDateTime";
                    break;
                case "Boolean":
                    dataType = "boolean";
                    break;
            }

            varsSB.append(String.format("\t\tprivate %s %s;\n\n", dataType, varName));

            String methodName = table.getLongName().replace(" ", "");
            //getter
            methodsSB.append(String.format("\t\tpublic %s get%s() {\n\t\t\treturn %s;\n\t\t}\n\n",
                    dataType, methodName, varName));
            //setter
            methodsSB.append(String.format("\t\tpublic void set%s(%s %s) {\n\t\t\tthis.%s = %s;\n\t\t}\n\n",
                    methodName, dataType, varName, varName, varName));

        });

        //replace tabs with two spaces
        String vars = varsSB.toString().replace("\t", "  ");
        String methods = methodsSB.toString().replace("\t", "  ");

        Map<String, String> codeMap = new LinkedHashMap<>();

        codeMap.put("vars", vars);
        codeMap.put("methods", methods);

        return codeMap;
    }

    public static String toCamelCase(String varName) {
        varName = varName.replace(" ", "");

        StringBuilder varNameSB = new StringBuilder();

        int strIdx = 0;
        while(strIdx < varName.length()) {
            //chars at the beginning of variable name should be lowercase
            boolean previouslyUppercase = false;
            char c = varName.charAt(strIdx);
            if (c >= 'A' && c <= 'Z') {
                c += 32;
                previouslyUppercase = true;
            }

            varNameSB.append(c);

            if (previouslyUppercase && strIdx + 1 < varName.length()) {
                char nextChar = varName.charAt(strIdx + 1);
                //if next char is lowercase, continue
                if (nextChar >= 'a' && nextChar <= 'z') {
                    strIdx++;
                    continue;
                }
                else if (strIdx + 2 < varName.length()){
                    nextChar = varName.charAt(strIdx + 2);
                    //check to see if the following character is lowercase, if so break
                    if (nextChar >= 'a' && nextChar <= 'z') {
                        strIdx++;
                        break;
                    }
                }
            }
            else if (strIdx + 1 < varName.length()){
                char nextChar = varName.charAt(strIdx + 1);
                //if next char is lowercase, put uppercase back and break
                if (nextChar >= 'a' && nextChar <= 'z') {
                    varNameSB.deleteCharAt(strIdx);
                    break;
                }
            }

            strIdx++;
        }

        //append remaining characters to
        varNameSB.append(varName.substring(strIdx));
        return varNameSB.toString();
    }

    public static String getDataType(String retsDataType) {

        switch (retsDataType) {
            case "Character":
                return "String";
            case "Tiny":
            case "Small":
                return "short";
            case "Int":
                return "int";
            case "Long":
                return "long";
            case "Decimal":
                return "double";
            case "Date":
            case "DateTime":
            case "Time":
                return "Date";
            case "Boolean":
                return "boolean";
        }

        return "UNK";
    }

}
