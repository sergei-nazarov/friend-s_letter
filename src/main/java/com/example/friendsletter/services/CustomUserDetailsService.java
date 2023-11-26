package com.example.friendsletter.services;

import com.example.friendsletter.data.Role;
import com.example.friendsletter.data.User;
import com.example.friendsletter.data.UserDto;
import com.example.friendsletter.errors.USER_ERRORS;
import com.example.friendsletter.errors.UserErrorHolder;
import com.example.friendsletter.errors.UserUpdateException;
import com.example.friendsletter.repository.RoleRepository;
import com.example.friendsletter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService, IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = loadUserByUsernameOrEmail(username, username);
        if (user == null) {
            throw new UsernameNotFoundException("Can't authenticate user " + username);
        }
        return user;
    }

    @Override
    public User loadUserByUsernameOrEmail(String username, String email) throws UsernameNotFoundException {
        return userRepository.findFirstByUsernameOrEmail(username, email);
    }

    @Override
    public User saveUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role role = roleRepository.findByName("ROLE_USER");
        if (role == null) {
            role = checkRoleExist();
        }
        user.setRoles(List.of(role));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user, UserDto updatedDto) throws UserUpdateException {
        List<UserErrorHolder> errors = new ArrayList<>();

        String newUsername = updatedDto.getUsername();
        if (!user.getUsername().equals(newUsername)) {
            User byUsername = userRepository.findByUsername(newUsername);
            if (byUsername == null) {
                user.setUsername(newUsername);
            } else {
                errors.add(new UserErrorHolder(USER_ERRORS.USERNAME_ALREADY_REGISTERED, newUsername));
            }
        }

        String newEmail = updatedDto.getEmail();
        if (!user.getEmail().equals(newEmail)) {
            User byEmail = userRepository.findByEmail(newEmail);
            if (byEmail == null) {
                user.setEmail(newEmail);
            } else {
                errors.add(new UserErrorHolder(USER_ERRORS.EMAIL_ALREADY_REGISTERED, newEmail));
            }
        }
        if (!updatedDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(updatedDto.getPassword()));
        }
        if (errors.size() == 0) {
            return userRepository.save(user);
        } else {
            throw new UserUpdateException(errors);
        }
    }

    private Role checkRoleExist() {
        Role role = new Role();
        role.setName("ROLE_USER");
        return roleRepository.save(role);
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
