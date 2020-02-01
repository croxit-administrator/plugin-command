package io.croxit.payment.gateway.plugin.command.helper;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import java.util.*;

public class ErrorHelper {

    public static final String SEPARATOR = ".";
    public static final String PATH = "path";

    public static <T> Map<String, List<String>> from(Set<ConstraintViolation<T>> constraintViolations) {
        Map<String, List<String>> map = new HashMap<>(constraintViolations.size());

        constraintViolations.forEach(violation -> {
            for (String attribute : getAttributes(violation)) {
                putEntry(map, attribute, violation.getMessage());
            }
        });

        return map;
    }

    private static void putEntry(Map<String, List<String>> map, String key, String value) {
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList<>());
        }
        map.get(key).add(value);
    }

    private static String[] getAttributes(ConstraintViolation<?> constraintViolation) {
        String[] values = (String[]) constraintViolation.getConstraintDescriptor().getAttributes().get(PATH);
        if (values == null || values.length == 0) {
            return getAttributesFromPath(constraintViolation);
        } else {
            return values;
        }
    }

    private static String[] getAttributesFromPath(ConstraintViolation<?> constraintViolation) {
        Path path = constraintViolation.getPropertyPath();

        StringBuilder builder = new StringBuilder();
        path.forEach(node -> {
            if (node.getName() != null) {
                if (builder.length() > 0) {
                    builder.append(SEPARATOR);
                }

                builder.append(node.getName());
            }
        });

        return new String[]{builder.toString()};
    }

}

