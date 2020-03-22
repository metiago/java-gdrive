package io.zbx;

import io.zbx.configs.AppConfig;
import io.zbx.dto.RoleDTO;
import io.zbx.dto.UserDTO;
import io.zbx.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@Import({AppConfig.class})
public class Application implements CommandLineRunner {

    @Autowired
    private UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        UserDTO user = new UserDTO();
        user.setUsername("admin");
        user.setPassword("admin");
        RoleDTO role = new RoleDTO();
        role.setName("ADMIN");
        role.setDescription("Role for admin users.");
        user.setRoles(Stream.of(role).collect(Collectors.toSet()));
        userService.save(user);
    }
}
