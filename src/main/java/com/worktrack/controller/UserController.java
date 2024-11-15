package com.worktrack.controller;
import com.worktrack.dto.EmployeeDto;
import com.worktrack.exception.UserNotFoundException;
import com.worktrack.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/auth/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping
    public List<EmployeeDto> findAllEmployees() {
        return userService.findAllEmployees();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id){
        try{
            EmployeeDto employee = userService.findById(id);
            return ResponseEntity.ok(employee);
        } catch(UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") long id, @Valid @RequestBody EmployeeDto employeeDto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> {
                errorMessages.append(error.getDefaultMessage()).append("\n");
            });
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages.toString());
        }

        try{
            EmployeeDto updatedEmployeeDto = userService.updateUser(id, employeeDto);
            return ResponseEntity.ok(updatedEmployeeDto);
        } catch(UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occured: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        try{
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch(UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found.");
        }
    }
}

