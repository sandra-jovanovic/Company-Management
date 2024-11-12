package com.worktrack.mapper;

import com.worktrack.dto.EmployeeDto;
import com.worktrack.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

   private final ModelMapper modelMapper;


    public UserMapper() {
        this.modelMapper = new ModelMapper();
    }

    public EmployeeDto toEmployeeDto(User user){
        return modelMapper.map(user, EmployeeDto.class);
    }

    public User toUser(EmployeeDto employeeDto){
        return modelMapper.map(employeeDto, User.class);
    }
}
