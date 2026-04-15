package com.example.chat.user.mapper;

import com.example.chat.user.domain.ProfileColor;
import com.example.chat.user.domain.Role;
import com.example.chat.user.domain.User;
import com.example.chat.user.domain.UserStatus;
import com.example.chat.user.dto.UserCreateRequest;
import com.example.chat.user.dto.UserInfoProjection;
import com.example.chat.user.dto.UserInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {UserStatus.class, Role.class, ProfileColor.class})
public interface UserMapper {

    @Mapping(target = "password", source = "encodedPassword")
    @Mapping(target = "username", source = "initialUsername")
    @Mapping(target = "nickname", source = "initialUsername")
    @Mapping(target = "profileIconColor", expression = "java(ProfileColor.getRandomHexCode())")
    @Mapping(target = "status", expression = "java(UserStatus.ACTIVE)")
    User toEntity(UserCreateRequest request, String encodedPassword, String initialUsername);

    UserInfoResponse toResponse(UserInfoProjection projection);
}
