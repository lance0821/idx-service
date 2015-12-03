package idxsync.sync.strategy;

import idxsync.domain.IdxDomain;
import idxsync.sync.service.SyncServiceException;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class SyncUtils {

    public static void refreshDomain(IdxDomain domain2Refresh, IdxDomain freshDomain) {
        Field[] domainFields = freshDomain.getClass().getDeclaredFields();

        for(Field domainField : domainFields) {
            try {

                domainField.setAccessible(true);
                Field field2Refresh = domain2Refresh.getClass().getDeclaredField(domainField.getName());
                field2Refresh.setAccessible(true);
                Object val = domainField.get(freshDomain);
                field2Refresh.set(domain2Refresh, val);

            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new SyncServiceException(e);
            }
        }
    }

    public static boolean domainNeedsRefresh(IdxDomain domain2Refresh, IdxDomain freshDomain) {
        String domain2RefreshHash = getDomainHash(domain2Refresh);
        String freshDomainHash = getDomainHash(freshDomain);

        return !domain2RefreshHash.equals(freshDomainHash);
    }

    public static String getDomainHash(IdxDomain domain) {

        //compute hash input
        StringBuilder hashInputSb = new StringBuilder();
        List<Field> fields = Arrays.asList(domain.getClass().getDeclaredFields());
        for(Field field : fields) {
            field.setAccessible(true);
            try {
                Object obj = field.get(domain);
                if (obj == null) {
                    hashInputSb.append("");
                }
                else {
                    hashInputSb.append(obj.toString());
                }
            } catch (IllegalAccessException e) {
                throw new SyncServiceException(e);
            }
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA");

            return Base64.getEncoder().encodeToString(digest.digest(hashInputSb.toString().getBytes()));

        } catch (NoSuchAlgorithmException e) {
            throw new SyncServiceException(e);
        }
    }
}
