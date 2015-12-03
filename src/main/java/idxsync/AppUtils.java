package idxsync;


import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class AppUtils {

    public static <T>List<T> castCollectionList(List srcList, Class<T> cls){
        List<T> list =new ArrayList<T>();
        for (Object obj : srcList) {
            if(obj!=null && cls.isAssignableFrom(obj.getClass()))
                list.add(cls.cast(obj));
        }
        return list;
    }

    public static <T>Set<T> castCollectionSet(List srcList, Class<T> cls){
        Set<T> set = new HashSet<>();
        for (Object obj : srcList) {
            if(obj!=null && cls.isAssignableFrom(obj.getClass()))
                set.add(cls.cast(obj));
        }
        return set;
    }

    /**
     * Returns any declared field in class hierarchy
     * @param fieldName Name of field to retrieve
     * @param type Class type
     * @return Requested Field if it exists
     */
    public static Field getDeclaredField(String fieldName, Class<?> type) {
        List<Field> allFields = getAllFields(type);

        Field retField = null;
        for(Field field : allFields) {
            if (field.getName().equals(fieldName)) {
                retField = field;
                break;
            }
        }

        return retField;
    }

    public static List<Field> getAllFields(Class<?> type) {
        return getAllFields(new ArrayList<>(), type);
    }

    private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            fields = getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    public static Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static Long getTime(Date date) {
        return date.getTime();
    }

    public static Long getTime(LocalDateTime dateTime) {
        return getTime(toDate(dateTime));
    }
}
