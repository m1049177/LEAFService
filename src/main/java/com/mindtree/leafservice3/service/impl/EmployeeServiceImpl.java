package com.mindtree.leafservice3.service.impl;

import com.mindtree.leafservice3.service.EmployeeService;
import com.mindtree.leafservice3.domain.Employee;
import com.mindtree.leafservice3.repository.EmployeeRepository;
import com.mindtree.leafservice3.repository.search.EmployeeSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Employee}.
 */
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;

    private final EmployeeSearchRepository employeeSearchRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeSearchRepository employeeSearchRepository) {
        this.employeeRepository = employeeRepository;
        this.employeeSearchRepository = employeeSearchRepository;
    }

    /**
     * Save a employee.
     *
     * @param employee the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Employee save(Employee employee) {
        log.debug("Request to save Employee : {}", employee);
        Employee result = employeeRepository.save(employee);
        employeeSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the employees.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        log.debug("Request to get all Employees");
        return employeeRepository.findAll();
    }


    /**
     * Get one employee by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Employee> findOne(Long id) {
        log.debug("Request to get Employee : {}", id);
        return employeeRepository.findById(id);
    }

    /**
     * Delete the employee by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Employee : {}", id);
        employeeRepository.deleteById(id);
        employeeSearchRepository.deleteById(id);
    }

    /**
     * Search for the employee corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Employee> search(String query) {
        log.debug("Request to search Employees for query {}", query);
        return StreamSupport
            .stream(employeeSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
