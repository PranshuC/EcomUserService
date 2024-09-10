package com.pranshu.ecomuserservice.mapper;

import com.pranshu.ecomuserservice.dto.UserDto;
import com.pranshu.ecomuserservice.model.User;

public class UserEntityDTOMapper {
    public static UserDto getUserDTOFromUserEntity(User user){
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles());
        return userDto;
    }
}
