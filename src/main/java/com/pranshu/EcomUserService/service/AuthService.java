package com.pranshu.EcomUserService.service;

import com.pranshu.EcomUserService.dto.UserDto;
import com.pranshu.EcomUserService.exception.InvalidCredentialException;
import com.pranshu.EcomUserService.exception.InvalidTokenException;
import com.pranshu.EcomUserService.exception.UserNotFoundException;
import com.pranshu.EcomUserService.mapper.UserEntityDTOMapper;
import com.pranshu.EcomUserService.model.Session;
import com.pranshu.EcomUserService.model.SessionStatus;
import com.pranshu.EcomUserService.model.User;
import com.pranshu.EcomUserService.repository.SessionRepository;
import com.pranshu.EcomUserService.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.*;

@Service
public class AuthService {
    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(UserRepository userRepository, SessionRepository sessionRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public ResponseEntity<UserDto> login(String email, String password) {
        //Get user details from DB
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User for the given email id does not exist");
        }
        User user = userOptional.get();

        //Verify the user password given at the time of login
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialException("Invalid Credentials");
        }

        //token generation
        //String token = RandomStringUtils.randomAlphanumeric(30);
        MacAlgorithm alg = Jwts.SIG.HS256; // HS256 algo added for JWT
        SecretKey key = alg.key().build(); // generating the secret key

        //start adding the claims
        Map<String, Object> jsonForJWT = new HashMap<>();
        jsonForJWT.put("userId", user.getId());
        jsonForJWT.put("roles", user.getRoles());
        jsonForJWT.put("createdAt", new Date());
        jsonForJWT.put("expiryAt", new Date(LocalDate.now().plusDays(3).toEpochDay()));

        String token = Jwts.builder()
                .claims(jsonForJWT) // added the claims
                .signWith(key, alg) // added the algo and key
                .compact(); //building the token

        //session creation
        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);
        session.setLoginAt(new Date());
        sessionRepository.save(session);

        //generating the response
        UserDto userDto = UserEntityDTOMapper.getUserDTOFromUserEntity(user);

        //setting up the headers
        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, token);
        return new ResponseEntity<>(userDto, headers, HttpStatus.OK);
    }

    public ResponseEntity<Void> logout(String token, Long userId) {
        // validations -> token exists, token is not expired, user exists else throw an exception
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);
        if (sessionOptional.isEmpty()) {
            return null; //TODO throw exception here
        }
        Session session = sessionOptional.get();
        session.setSessionStatus(SessionStatus.ENDED);
        sessionRepository.save(session);
        return ResponseEntity.ok().build();
    }

    public UserDto signUp(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        User savedUser = userRepository.save(user);
        return UserDto.from(savedUser);
    }

    public SessionStatus validate(String token, Long userId) {
        //TODO check expiry // Jwts Parser -> parse the encoded JWT token to read the claims

        //verifying from DB if session exists
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);
        if (sessionOptional.isEmpty() || sessionOptional.get().getSessionStatus().equals(SessionStatus.ENDED)) {
            throw new InvalidTokenException("token is invalid");
        }
        return SessionStatus.ACTIVE;
    }

    public ResponseEntity<List<Session>> getAllSession(){
        List<Session> sessions = sessionRepository.findAll();
        return ResponseEntity.ok(sessions);
    }

    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(userRepository.findAll());
    }

}

/*
    MultiValueMapAdapter is map with single key and multiple values
    Recommended Data Structure for Headers
    Key     Value
    Token   ""
    Accept  application/json, text, images

    "Set-Cookie" is an industry standard key-name
    whenever client calls the server, it expects a token in this response header
 */