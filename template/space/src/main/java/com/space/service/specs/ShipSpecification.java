package com.space.service.specs;

import com.space.model.Ship;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShipSpecification implements Specification<Ship> {
    private final List<Filter> filters;

    public ShipSpecification() {
        this.filters = new ArrayList<>();
    }

    public void add(Filter filter) {
        filters.add(filter);
    }

    @Override
    public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        for (Filter filter : filters) {
            if (filter.getOperator().equals(QueryOperator.EQUAL)) {
                predicates.add(builder.equal(
                        root.get(filter.getKey()), filter.getValue()));
            } else if (filter.getOperator().equals(QueryOperator.MATCH)) {
                predicates.add(builder.like(
                        builder.lower(root.get(filter.getKey())),
                        "%" + filter.getValue().toString().toLowerCase() + "%"));
            } else if (filter.getOperator().equals(QueryOperator.GREATER_THAN_EQUAL)) {
                predicates.add(builder.greaterThanOrEqualTo(
                        root.get(filter.getKey()), filter.getValue().toString()));
            } else if (filter.getOperator().equals(QueryOperator.LESS_THAN_EQUAL)) {
                predicates.add(builder.lessThanOrEqualTo(
                        root.get(filter.getKey()), filter.getValue().toString()));
            }  else if (filter.getOperator().equals(QueryOperator.AFTER)) {
                predicates.add(builder.greaterThanOrEqualTo(
                        root.get(filter.getKey()), (Date)filter.getValue()));
            } else if (filter.getOperator().equals(QueryOperator.BEFORE)) {
                predicates.add(builder.lessThanOrEqualTo(
                        root.get(filter.getKey()), (Date)filter.getValue()));
            }

        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }
}