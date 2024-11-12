package com.worktrack.mapper;

import com.worktrack.dto.EmployeeDto;
import com.worktrack.dto.LeaveRequestDto;
import com.worktrack.model.LeaveRequest;
import com.worktrack.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LeaveRequestMapper {

    private final ModelMapper modelMapper;

    public LeaveRequestMapper() {
        this.modelMapper = new ModelMapper();
    }

    @Autowired
    private UserMapper userMapper;

    public LeaveRequestDto toLeaveRequestDto(LeaveRequest leaveRequest){
        LeaveRequestDto leaveRequestDto = modelMapper.map(leaveRequest, LeaveRequestDto.class);

        EmployeeDto employeeDto = userMapper.toEmployeeDto(leaveRequest.getUser());
        leaveRequestDto.setEmployeeDto(employeeDto);

        return leaveRequestDto;
    }

    public LeaveRequest toLeaveRequest(LeaveRequestDto leaveRequestDto){
        LeaveRequest leaveRequest = modelMapper.map(leaveRequestDto, LeaveRequest.class);

        User user = modelMapper.map(leaveRequestDto.getEmployeeDto(), User.class);
        leaveRequest.setUser(user);

        return leaveRequest;
    }


}
