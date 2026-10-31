import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.annotation.Id;

public class Repository<T> {

    private final Map<String, T> items = new HashMap<>();

    // Save one entity
    public synchronized T save(T entity) {

        if (entity == null) {
            throw new IllegalArgumentException(
                    "Entity cannot be null."
            );
        }

        String id = readId(entity);

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Entity ID cannot be null or empty."
            );
        }

        items.put(id, entity);

        return entity;
    }

    // Save multiple entities
    @SafeVarargs
    public final synchronized void saveAll(T... entities) {

        if (entities == null) {
            throw new IllegalArgumentException(
                    "Entity array cannot be null."
            );
        }

        for (T entity : entities) {
            save(entity);
        }
    }

    // Find an entity using its ID
    public synchronized T findById(String id) {

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "ID cannot be null or empty."
            );
        }

        return items.get(id);
    }

    // Retrieve all entities
    public synchronized ArrayList<T> findAll() {

        return new ArrayList<>(items.values());
    }

    // Delete an entity using its ID
    public synchronized boolean delete(String id) {

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "ID cannot be null or empty."
            );
        }

        return items.remove(id) != null;
    }

    // Get total number of entities
    public synchronized int size() {

        return items.size();
    }

    // Check whether repository is empty
    public synchronized boolean isEmpty() {

        return items.isEmpty();
    }

    // Find the field marked with @Id using Reflection
    private String readId(T entity) {

        Class<?> currentClass = entity.getClass();

        while (currentClass != null) {

            for (Field field : currentClass.getDeclaredFields()) {

                if (field.isAnnotationPresent(Id.class)) {

                    try {

                        field.setAccessible(true);

                        Object value = field.get(entity);

                        if (value == null) {
                            return null;
                        }

                        return value.toString();

                    } catch (IllegalAccessException e) {

                        throw new RuntimeException(
                                "Unable to access entity ID.",
                                e
                        );
                    }
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        throw new IllegalArgumentException(
                "Entity class must contain a field annotated with @Id."
        );
    }

    // Generic method to calculate the average of Number values
    public static <N extends Number> double average(N[] values) {

        if (values == null || values.length == 0) {
            return 0.0;
        }

        double sum = 0;

        for (N value : values) {

            if (value == null) {
                throw new IllegalArgumentException(
                        "Array cannot contain null values."
                );
            }

            sum += value.doubleValue();
        }

        return sum / values.length;
    }
}