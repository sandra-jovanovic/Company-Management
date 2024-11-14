package com.worktrack.service;

import com.worktrack.dto.EmployeeDto;
import com.worktrack.exception.UserNotFoundException;
import com.worktrack.mapper.UserMapper;
import com.worktrack.model.User;
import com.worktrack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById_UserFound(){
        User user = new User();
        user.setId(1L);
        user.setFirstname("Sandra");
        user.setLastname("Jovanovic");
        user.setEmail("s@j");

        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setFirstname("Sandra");
        employeeDto.setLastname("Jovanovic");
        employeeDto.setEmail("s@j");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toEmployeeDto(user)).thenReturn(employeeDto);

        EmployeeDto result = userService.findById(1L);

        assertNotNull(result);
        assertEquals("Sandra", result.getFirstname());
        assertEquals("Jovanovic", result.getLastname());
        assertEquals("s@j", result.getEmail());
    }

    @Test
    void testFindById_UserNotFound(){

        when(userRepository.existsById(1L)).thenReturn(Optional.empty().isEmpty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void testDeleteUser_UserFound(){
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        Mockito.verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUser_UserNotFound(){
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void testUpdateUser_UserFound(){
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setFirstname("Sandra");
        existingUser.setLastname("Jovanovic");
        existingUser.setEmail("s@j");

        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setFirstname("SANDRA");
        employeeDto.setLastname("JOVANOVIC");
        employeeDto.setEmail("s@jov");

        when(userRepository.findUserById(1L)).thenReturn(existingUser);
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userMapper.toEmployeeDto(existingUser)).thenReturn(employeeDto);

        EmployeeDto result = userService.updateUser(1L, employeeDto);

        assertEquals("SANDRA", result.getFirstname());
        assertEquals("JOVANOVIC", result.getLastname());
        assertEquals("s@jov", result.getEmail());
    }

    @Test
    void testUpdateUser_UserNotFound(){
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setFirstname("Sandra");
        employeeDto.setLastname("Jovanovic");
        employeeDto.setEmail("s@j");

        when(userRepository.findUserById(1L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, employeeDto));
    }













































}
