package com.outwork.accountingapiapp.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.outwork.accountingapiapp.services.MessageService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Util {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MessageService messageService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public String getSimpleMessage(String title, String content, Locale locale) {
        if (StringUtils.isEmpty(title) && StringUtils.isEmpty(content)) {
            return "";
        }

        if (StringUtils.isEmpty(title)) {
            return messageService.getMessage(content, locale);
        }

        if (StringUtils.isEmpty(content)) {
            return messageService.getMessage(title, locale);
        }

        return messageService.getMessage(title, locale) + ": " + messageService.getMessage(content, locale);
    }

    public <T> List<Double> getSumsBySpecifications(List<Specification<T>> specs, List<String> attributeNames, Class<T> entityClass) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createQuery(Tuple.class);
        Root<T> root = criteriaQuery.from(entityClass);

        List<Expression<Double>> sumExpressions = attributeNames.stream()
                .map(attributeName -> criteriaBuilder.sum(root.get(attributeName)).as(Double.class))
                .toList();

        criteriaQuery.multiselect(sumExpressions.toArray(new Expression[0]));
        criteriaQuery.where(getPredicateForAttributes(specs, root, criteriaQuery, criteriaBuilder));

        Query query = entityManager.createQuery(criteriaQuery);
        Tuple result = (Tuple) query.getSingleResult();

        return sumExpressions.stream()
                .map(result::get)
                .collect(Collectors.toList());
    }

    private <T> Predicate getPredicateForAttributes(List<Specification<T>> specs, Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Predicate[] predicates = specs.stream()
                .map(spec -> spec.toPredicate(root, criteriaQuery, criteriaBuilder))
                .toArray(Predicate[]::new);
        return criteriaBuilder.and(predicates);
    }
    public <T> Map<Object, Double> getGroupedSumsBySpecification(Specification<T> spec, String groupByField, String sumField, Class<T> entityClass) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createQuery(Tuple.class);
        Root<T> root = criteriaQuery.from(entityClass);

        Expression<Object> groupByExpression = root.get(groupByField);
        Expression<Double> sumExpression = criteriaBuilder.sum(root.get(sumField));

        criteriaQuery.multiselect(groupByExpression, sumExpression);
        criteriaQuery.groupBy(groupByExpression);
        criteriaQuery.where(spec.toPredicate(root, criteriaQuery, criteriaBuilder));

        Query query = entityManager.createQuery(criteriaQuery);
        List<Tuple> resultList = query.getResultList();

        return resultList.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0), // Group by field
                        tuple -> (Double) tuple.get(1) // Sum field
                ));
    }

    public static double numberStandardRound (double number) {
        return Math.round(number);
    }

    public static boolean isPeriodGreaterThanSomeMonths(Date fromDate, Date toDate, int month) {
        // Create a Calendar instance and set it to the fromDate
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);

        // Add 2 months to the fromDate
        calendar.add(Calendar.MONTH, month);

        // Get the new date after adding 2 months
        Date dateAfterTwoMonths = calendar.getTime();

        // Compare the adjusted fromDate with toDate
        return toDate.after(dateAfterTwoMonths);
    }

    public String castObjectToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "Cannot interpret the object as json";
        }
    }
}
