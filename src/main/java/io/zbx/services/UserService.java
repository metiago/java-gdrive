package io.zbx.services;

import io.zbx.mapper.DataTransferObjectMapper;
import io.zbx.models.Role;
import io.zbx.repositories.UserRepository;
import io.zbx.models.User;
import io.zbx.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;


@Service(value = "userService")
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), getAuthority(user.get()));

        }

        throw new UsernameNotFoundException("Invalid username or password.");
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
        return authorities;
    }

    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        userRepository.findAll().iterator().forEachRemaining(list::add);
        return list;
    }


    public void delete(long id) {
        userRepository.deleteById(id);
    }


    public User findOne(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }


    public User findById(Long id) {
        return userRepository.findById(id).get();
    }


    public User save(UserDTO userDTO) {
        User user = DataTransferObjectMapper.map(userDTO, User.class);
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
