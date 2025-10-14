package id.perumdamts.kepegawaian.utils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public class SpecificationBuilder<T> {
    private final List<BiFunction<Root<T>, CriteriaBuilder, Predicate>> predicates = new ArrayList<>();

    public static <T> SpecificationBuilder<T> of() {
        return new SpecificationBuilder<>();
    }

    // Generic equal method
    public <V> SpecificationBuilder<T> addEqual(V value, String attribute) {
        if (value != null) {
            predicates.add((root, cb) -> {
                if (value instanceof String) {
                    return cb.equal(cb.lower(root.get(attribute)), ((String) value).toLowerCase());
                }
                return cb.equal(root.get(attribute), value);
            });
        }
        return this;
    }

    // Equal untuk nested attributes
    public <V> SpecificationBuilder<T> addEqual(V value, String... nestedAttributes) {
        if (value != null) {
            addCustom((root, cb) -> {
                Path<String> path = buildNestedPath(root, nestedAttributes);
                if (value instanceof String) {
                    return cb.equal(cb.lower(path), ((String) value).toLowerCase());
                }
                return cb.equal(path, value);
            });
        }
        return this;
    }

    // Like method
    public SpecificationBuilder<T> addLike(String value, String attribute) {
        if (StringUtils.hasText(value)) {
            predicates.add((root, cb) ->
                    cb.like(cb.lower(root.get(attribute)), "%" + value.toLowerCase() + "%"));
        }
        return this;
    }

    // Like untuk nested attributes
    public SpecificationBuilder<T> addLike(String value, String... nestedAttributes) {
        if (StringUtils.hasText(value)) {
            addCustom((root, cb) -> {
                Path<String> path = buildNestedPath(root, nestedAttributes);
                return cb.like(cb.lower(path), "%" + value.toLowerCase() + "%");
            });
        }
        return this;
    }

    // Comparison methods untuk regular attributes
    public <V extends Comparable<? super V>> SpecificationBuilder<T> addLessThan(V value, String attribute) {
        if (value != null) {
            predicates.add((root, cb) -> cb.lessThan(root.get(attribute), value));
        }
        return this;
    }

    public <V extends Comparable<? super V>> SpecificationBuilder<T> addGreaterThan(V value, String attribute) {
        if (value != null) {
            predicates.add((root, cb) -> cb.greaterThan(root.get(attribute), value));
        }
        return this;
    }

    public <V extends Comparable<? super V>> SpecificationBuilder<T> addGreaterThanOrEqual(V value, String attribute) {
        if (value != null) {
            predicates.add((root, cb) -> cb.greaterThanOrEqualTo(root.get(attribute), value));
        }
        return this;
    }

    public <V extends Comparable<? super V>> SpecificationBuilder<T> addLessThanOrEqual(V value, String attribute) {
        if (value != null) {
            predicates.add((root, cb) -> cb.lessThanOrEqualTo(root.get(attribute), value));
        }
        return this;
    }

    // Comparison methods untuk NESTED attributes
    public <V extends Comparable<? super V>> SpecificationBuilder<T> addGreaterThanOrEqual(
            V value, String... nestedAttributes) {
        if (value != null) {
            addCustom((root, cb) -> {
                Path<V> path = buildNestedPath(root, nestedAttributes);
                return cb.greaterThanOrEqualTo(path, value);
            });
        }
        return this;
    }

    public <V extends Comparable<? super V>> SpecificationBuilder<T> addLessThan(
            V value, String... nestedAttributes) {
        if (value != null) {
            addCustom((root, cb) -> {
                Path<V> path = buildNestedPath(root, nestedAttributes);
                return cb.lessThan(path, value);
            });
        }
        return this;
    }

    public <V extends Comparable<? super V>> SpecificationBuilder<T> addLessThanOrEqual(
            V value, String... nestedAttributes) {
        if (value != null) {
            addCustom((root, cb) -> {
                Path<V> path = buildNestedPath(root, nestedAttributes);
                return cb.lessThanOrEqualTo(path, value);
            });
        }
        return this;
    }

    public <V extends Comparable<? super V>> SpecificationBuilder<T> addGreaterThan(
            V value, String... nestedAttributes) {
        if (value != null) {
            addCustom((root, cb) -> {
                Path<V> path = buildNestedPath(root, nestedAttributes);
                return cb.greaterThan(path, value);
            });
        }
        return this;
    }

    // Range methods
    public <V extends Comparable<? super V>> SpecificationBuilder<T> addBetween(
            V start, V end, String attribute) {
        if (start != null && end != null) {
            predicates.add((root, cb) -> cb.between(root.get(attribute), start, end));
        } else if (start != null) {
            addGreaterThanOrEqual(start, attribute);
        } else if (end != null) {
            addLessThanOrEqual(end, attribute);
        }
        return this;
    }

    public <V extends Comparable<? super V>> SpecificationBuilder<T> addBetween(
            V start, V end, String... nestedAttributes) {
        if (start != null && end != null) {
            addCustom((root, cb) -> {
                Path<V> path = buildNestedPath(root, nestedAttributes);
                return cb.between(path, start, end);
            });
        } else if (start != null) {
            addGreaterThanOrEqual(start, nestedAttributes);
        } else if (end != null) {
            addLessThanOrEqual(end, nestedAttributes);
        }
        return this;
    }

    // IN clause method
    @SafeVarargs
    public final <V> SpecificationBuilder<T> addIn(String attribute, V... values) {
        if (values != null && values.length > 0) {
            predicates.add((root, cb) -> root.get(attribute).in((Object[]) values));
        }
        return this;
    }

    // Null check methods
    public SpecificationBuilder<T> addIsNull(String attribute) {
        predicates.add((root, cb) -> cb.isNull(root.get(attribute)));
        return this;
    }

    public SpecificationBuilder<T> addIsNotNull(String attribute) {
        predicates.add((root, cb) -> cb.isNotNull(root.get(attribute)));
        return this;
    }

    // Custom predicate method
    public SpecificationBuilder<T> addCustom(BiFunction<Root<T>, CriteriaBuilder, Predicate> predicateFunction) {
        predicates.add(predicateFunction);
        return this;
    }

    // Build nested path dengan validasi
    private <X> Path<X> buildNestedPath(Root<T> root, String[] nestedAttributes) {
        if (nestedAttributes == null || nestedAttributes.length == 0) {
            throw new IllegalArgumentException("Nested attributes cannot be null or empty");
        }

        Path<X> path = root.get(nestedAttributes[0]);
        for (int i = 1; i < nestedAttributes.length; i++) {
            path = path.get(nestedAttributes[i]);
        }
        return path;
    }

    // Optimized build method
    public Specification<T> build() {
        return (root, query, cb) -> {
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            Predicate[] predicateArray = predicates.stream()
                    .map(func -> func.apply(root, cb))
                    .filter(Objects::nonNull)
                    .toArray(Predicate[]::new);

            return predicateArray.length > 0 ? cb.and(predicateArray) : cb.conjunction();
        };
    }

    public int getPredicateCount() {
        return predicates.size();
    }

    public void clear() {
        predicates.clear();
    }
}