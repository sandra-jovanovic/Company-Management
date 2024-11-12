package com.worktrack.service;

import com.worktrack.dto.EmployeeDto;
import com.worktrack.exception.UserNotFoundException;
import com.worktrack.mapper.UserMapper;
import com.worktrack.model.User;
import com.worktrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;


    //radi
    public List<EmployeeDto> findAllEmployees() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toEmployeeDto)
                .toList();
    }

    public EmployeeDto findById(Long id) {
        Optional<User> _user = userRepository.findById(id);

        if(_user.isPresent()){
            return userMapper.toEmployeeDto(_user.get());
        } else {
            throw new UserNotFoundException("User not found");
        }
    }


     public void deleteUser(Long id) {
        boolean exists = userRepository.existsById(id);

        if(!exists){
            throw new UserNotFoundException("User not found");
        }
        userRepository.deleteById(id);
     }

    public EmployeeDto updateUser(Long id, EmployeeDto employeeDto){
        User user = userRepository.findUserById(id);

        if(user == null){
            throw new UserNotFoundException("User not found.");
        }

        user.setFirstname(employeeDto.getFirstname());
        user.setLastname(employeeDto.getLastname());
        user.setEmail(employeeDto.getEmail());

        userRepository.save(user);

        return userMapper.toEmployeeDto(user);
    }


}
