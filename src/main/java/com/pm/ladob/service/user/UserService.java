package com.pm.ladob.service.user;

import com.pm.ladob.dto.user.UserRequestDto;
import com.pm.ladob.dto.user.UserResponseDto;
import com.pm.ladob.enums.UserRole;
import com.pm.ladob.exceptions.AlreadyExistsException;
import com.pm.ladob.models.User;
import com.pm.ladob.repository.UserRepository;
import com.pm.ladob.service.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto createUser(UserRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("User already exists with email: " + request.getEmail());
        }

        request.setPassword(passwordEncoder.encode(request.getPassword()));

        User newUser = UserMapper.toModel(request);
        newUser.setActive(false);
        newUser.setRole(UserRole.USER);

        newUser = userRepository.save(newUser);

        return UserMapper.toDto(newUser);
    }

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
    }
}
