package com.rakx07.main.controller;

import com.rakx07.main.model.Authority;
import com.rakx07.main.model.RefreshToken;
import com.rakx07.main.model.User;
import com.rakx07.main.service.RefreshTokenService;
import com.rakx07.main.service.UserService;
import com.rakx07.main.util.JwtUtil;
import io.micrometer.common.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class RestApiController {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public RestApiController(JwtUtil jwtUtil, UserService userService, AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/user/register")
    public ResponseEntity<?> userRegister(@RequestBody User user) {
        User existingUser = userService.findUserByUsername(user.getUserName());

        if(existingUser != null) {
            return ResponseEntity.badRequest().body("User already exists!");
        }
        userService.saveUser(user);
        return ResponseEntity.ok("User registration successful!");
    }

    @PostMapping("/admin/register")
    public ResponseEntity<?> adminRegister(@RequestBody User user) {
        User existingUser = userService.findUserByUsername(user.getUserName());

        if(existingUser != null) {
            return ResponseEntity.badRequest().body("Admin already exists!");
        }
        userService.saveAdmin(user);
        return ResponseEntity.ok("Admin registration successful!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user){
        try {

            // Check if username + password is correct
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            User existingUser = userService.findUserByUsername(user.getUserName());

            // Create and return JWT token
            Map<String, Object> tokenMap = new HashMap<>();
            tokenMap.put("jwtToken", jwtUtil.generateToken(userDetails));
            tokenMap.put("refreshToken", refreshTokenService.createRefreshToken(existingUser).getToken());

            return new ResponseEntity<>(tokenMap, HttpStatus.OK);
        } catch (Exception ex) {
            Map<String, String> errorMap = Map.of("Error", "Invalid credentials");
            return new ResponseEntity<>(errorMap, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, Object> requestBody) {
        String refreshToken = requestBody.get("refreshToken").toString();

        if(StringUtils.isNotBlank(refreshToken))
        {
            // Here we are getting the whole object (refreshToken model ta)
            RefreshToken existingRefreshToken = refreshTokenService.findByRefreshToken(refreshToken);

            if(existingRefreshToken != null && refreshTokenService.isRefreshTokenValid(existingRefreshToken)) {
                // Create new JWT token

                // Here i can get user because User class has a onetoone relation with this token.
                User user = existingRefreshToken.getUser();

                Set<GrantedAuthority> authorities = new HashSet<>();
                for(Authority authority : user.getAuthorities()) {
                    SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority.getAuthority());
                    authorities.add(simpleGrantedAuthority);
                }
                UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                        user.getUserName(), "", authorities);

                String jwtToken = jwtUtil.generateToken(userDetails);

                Map<String, String> tokenResponse = Map.of("jwtToken", jwtToken);
                return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
                /**
                 * This can be used as an alternative way.
                 * return ResponseEntity.ok(Map.of("jwtToken", jwtToken));
                 */
            }
        }
        return ResponseEntity.badRequest().body("Invalid refresh token!");
    }
}
