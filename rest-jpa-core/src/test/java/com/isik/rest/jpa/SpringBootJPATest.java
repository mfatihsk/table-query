package com.isik.rest.jpa;

import com.isik.rest.jpa.api.Filter;
import com.isik.rest.jpa.api.QueryResult;
import com.isik.rest.jpa.api.Operator;
import com.isik.rest.jpa.api.QueryModel;
import com.isik.rest.jpa.models.City;
import com.isik.rest.jpa.models.Employee;
import com.isik.rest.jpa.models.Office;
import com.isik.rest.jpa.models.Title;
import com.isik.rest.jpa.repos.CityRepository;
import com.isik.rest.jpa.repos.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Sql(scripts ={"classpath:import_employee.sql"})
class SpringBootJPATest {

    private final static Logger LOG = LoggerFactory.getLogger(SpringBootJPATest.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void tableFilterTest() {
        List<City> all = cityRepository.findAll();

        Employee genericEntity = employeeRepository
                .save(new Employee("fatih", "isik", 180, new Date(), Title.MANAGER) );
        employeeRepository.findById(genericEntity.getId());

        List<Filter> filters = new ArrayList<>();
        QueryModel tableQuery = new QueryModel();
        Filter filter = new Filter();
        filter.setName("id");
        filter.setOperator(Operator.EQUALS);
        filter.setValue(genericEntity.getId());
        filters.add(filter);
        tableQuery.setFilters(filters);

        QueryResult<Employee> result = JPAQueryBuilder.filter(entityManager, tableQuery, Employee.class);
        assertEquals(1, result.getResults().size());

        filters.clear();
        filter = new Filter();
        filter.setName("name");
        filter.setOperator(Operator.CONTAINS);
        filter.setValue("tİh");
        filter.setLocale("tr");
        filters.add(filter);
        tableQuery.setFilters(filters);
        result = JPAQueryBuilder.filter(entityManager, tableQuery, Employee.class);
        assertEquals(2, result.getResults().size());

        filters.clear();
        filter = new Filter();
        filter.setName("name");
        filter.setOperator(Operator.NOT_CONTAINS);
        filter.setValue("tih");
        filters.add(filter);
        tableQuery.setFilters(filters);
        result = JPAQueryBuilder.filter(entityManager, tableQuery, Employee.class);
        assertEquals(2, result.getResults().size());

        filters.clear();
        filter = new Filter();
        filter.setName("office.city.id");
        filter.setOperator(Operator.EQUALS);
        filter.setValue("6");
        filters.add(filter);
        tableQuery.setFilters(filters);
        result = JPAQueryBuilder.filter(entityManager, tableQuery, Employee.class);
        assertEquals(2, result.getResults().size());

        filters.clear();
        filter = new Filter();
        filter.setName("addresses.city");
        filter.setOperator(Operator.EQUALS);
        filter.setValue("35");
        filters.add(filter);
        tableQuery.setFilters(filters);
        result = JPAQueryBuilder.filter(entityManager, tableQuery, Employee.class);
        assertEquals(1, result.getResults().size());
    }

    @Test
    void countTest() {
        QueryResult<Office> offices = JPAQueryBuilder.filter(entityManager, new QueryModel(), Office.class);
        assertEquals(30, offices.getTotalCount());
    }
    @Test
    void lowerCaseTest() {
        List<Filter> filters = new ArrayList<>();
        QueryModel tableQuery = new QueryModel();
        Filter filter = new Filter();
        filter.setName("name");
        filter.setOperator(Operator.CONTAINS);
        filter.setValue("dur");
        filter.setCaseInsensitive(true);
        filters.add(filter);
        tableQuery.setFilters(filters);

        QueryResult<Office> offices = JPAQueryBuilder.filter(entityManager, tableQuery, Office.class);
        assertEquals(1, offices.getTotalCount());
    }
    @Test
    void emptyResultTest() {
        List<Filter> filters = new ArrayList<>();
        QueryModel tableQuery = new QueryModel();
        Filter filter = new Filter();
        filter.setName("namexxxxx");
        filter.setOperator(Operator.CONTAINS);
        filter.setValue("dur");
        filter.setCaseInsensitive(true);
        filters.add(filter);
        tableQuery.setFilters(filters);

        QueryResult<Office> offices = JPAQueryBuilder.filter(entityManager, tableQuery, Office.class);
        assertEquals(0, offices.getTotalCount());
    }

    @Test
    void dateFilterTest() {
        List<Filter> filters = new ArrayList<>();
        QueryModel tableQuery = new QueryModel();
        Filter filter = new Filter();
        filter.setName("birthDate");
        filter.setOperator(Operator.GREATER_OR_EQUAL);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 1984);
        cal.set(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        filter.setValue(cal.getTime().getTime());
        filters.add(filter);
        tableQuery.setFilters(filters);

        QueryResult<Employee> offices = JPAQueryBuilder.filter(entityManager, tableQuery, Employee.class);
        assertEquals(2, offices.getTotalCount());

        cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 1985);
        filter.setValue(cal.getTime().getTime());

        offices = JPAQueryBuilder.filter(entityManager, tableQuery, Employee.class);
        assertEquals(1, offices.getTotalCount());
    }

    @Test
    void inTest() {
        List<Filter> filters = new ArrayList<>();
        QueryModel tableQuery = new QueryModel();
        Filter filter = new Filter();
        filter.setName("office");
        filter.setOperator(Operator.IN);
        filter.setValue("office-1,office-2");
        filters.add(filter);
        tableQuery.setFilters(filters);

        QueryResult<Employee> offices = JPAQueryBuilder.filter(entityManager, tableQuery, Employee.class);
        assertEquals(2, offices.getTotalCount());

        filters = new ArrayList<>();
        filter = new Filter();
        filter.setName("office.id");
        filter.setOperator(Operator.IN);
        filter.setValue("office-1,office-2");
        filters.add(filter);
        tableQuery.setFilters(filters);

        offices = JPAQueryBuilder.filter(entityManager, tableQuery, Employee.class);
        assertEquals(2, offices.getTotalCount());

        filters = new ArrayList<>();
        filter = new Filter();
        filter.setName("addresses.city");
        filter.setOperator(Operator.IN);
        filter.setValue("35");
        filters.add(filter);
        tableQuery.setFilters(filters);

        offices = JPAQueryBuilder.filter(entityManager, tableQuery, Employee.class);
        assertEquals(1, offices.getTotalCount());

        filters = new ArrayList<>();
        filter = new Filter();
        filter.setName("addresses.city.id");
        filter.setOperator(Operator.IN);
        filter.setValue("35");
        filters.add(filter);
        tableQuery.setFilters(filters);

        offices = JPAQueryBuilder.filter(entityManager, tableQuery, Employee.class);
        assertEquals(1, offices.getTotalCount());
    }


    @Test
    void notInTest() {
        List<Filter> filters = new ArrayList<>();
        QueryModel tableQuery = new QueryModel();
        Filter filter = new Filter();
        filter.setName("office");
        filter.setOperator(Operator.NOT_IN);
        filter.setValue("office-1,office-2");
        filters.add(filter);
        tableQuery.setFilters(filters);

        QueryResult<Employee> offices = JPAQueryBuilder.filter(entityManager, tableQuery, Employee.class);
        assertEquals(1, offices.getTotalCount());


    }
    // TODO MORE TESTS :)
}