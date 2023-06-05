package com.javaguides.springbootbackend.controller;

import com.javaguides.springbootbackend.model.Employee;
import com.javaguides.springbootbackend.model.ResponseObject;
import com.javaguides.springbootbackend.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1/")
public class EmployeeController {
    @Autowired
    private EmployeeRepository employeeRepository;

    //get all employee
    @GetMapping("/employees")
    public List<Employee> getAllEmployees()
    {
        return employeeRepository.findAll();
    }

    //get employee by id
    @GetMapping("/employees/{id}")
    ResponseEntity<ResponseObject> getEmployeeById (@PathVariable Long id)
    {
        Optional<Employee> foundEmployee = employeeRepository.findById(id);
        return foundEmployee.isPresent()?
                ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK","Query employee successfully",foundEmployee)
                ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed","Cannot find employee with id = "+id,"")
                );
    }

    //create employee
    @PostMapping("/employees")
    ResponseEntity<ResponseObject> createEmployee(@RequestBody Employee employee)
    {
//        return employeeRepository.save(employee);
        List<Employee> foundEmployeesLastName = employeeRepository.findByLastName(employee.getLastName().trim());
        List<Employee> foundEmployeesFirstName = employeeRepository.findByFirstName(employee.getFirstName().trim());
        if (foundEmployeesLastName.size()>0 && foundEmployeesFirstName.size()>0){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed","Employee name already taken","")
            );
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok","Insert Employee successfuly",employeeRepository.save(employee))
        );

    }

    //update employee
    @PutMapping("/employees/{id}")
    ResponseEntity<ResponseObject> updateEmployee(@PathVariable Long id, @RequestBody Employee newEmployee ){
        Employee updateEmployee = employeeRepository.findById(id).map(employee ->{
                employee.setFirstName(newEmployee.getFirstName());
                employee.setLastName(newEmployee.getLastName());
                employee.setEmailId(newEmployee.getEmailId());
                return employeeRepository.save(employee);
        }).orElseGet(()->{
                newEmployee.setId(id);
                return employeeRepository.save(newEmployee);
        });
        return ResponseEntity.status(HttpStatus.OK).body(
            new ResponseObject("OK","Update Employee successfully",newEmployee)
        );
    }

    @DeleteMapping("/employees/{id}")
    ResponseEntity<ResponseObject> deleteProduct(@PathVariable Long id)
    {
        boolean exists = employeeRepository.existsById(id);
        if (exists)
        {
            employeeRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK) .body(
                    new ResponseObject("ok","Delete product successfully","")
            );
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseObject("failed","Cannot find product to delete","")
        );

    }

}
