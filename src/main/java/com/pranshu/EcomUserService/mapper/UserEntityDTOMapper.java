package com.pranshu.EcomUserService.mapper;

import com.pranshu.EcomUserService.dto.UserDto;
import com.pranshu.EcomUserService.model.User;

public class UserEntityDTOMapper {
    public static UserDto getUserDTOFromUserEntity(User user){
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles());
        return userDto;
    }
}
