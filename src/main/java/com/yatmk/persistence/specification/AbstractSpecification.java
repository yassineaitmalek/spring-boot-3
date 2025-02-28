package com.yatmk.persistence.specification;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.jpa.domain.Specification;

public interface AbstractSpecification<T> {

  public default String like(String element) {
    return Optional.ofNullable(element).map(e -> "%" + e + "%").orElseGet(() -> "%%");
  }

  public default <W, U> Optional<Specification<U>> transformer(
      W object, Function<W, Specification<U>> mapper) {
    return Optional.ofNullable(object)
        .map(e -> Optional.ofNullable(mapper.apply(e)))
        .orElseGet(Optional::empty);
  }

  public default Specification<T> distinct() {
    return (root, query, builder) -> {
      query.distinct(true);
      return builder.isNotNull(root);
    };
  }

  public default Specification<T> whereDistinct() {
    return Specification.where(distinct());
  }

  public default Specification<T> unitSpecification() {
    return (root, query, builder) -> builder.isNotNull(root);
  }

}
