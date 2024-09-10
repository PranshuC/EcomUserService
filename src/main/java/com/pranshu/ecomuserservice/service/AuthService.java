package com.pranshu.ecomuserservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pranshu.ecomuserservice.dto.JwtPayloadDTO;
import com.pranshu.ecomuserservice.dto.UserDto;
import com.pranshu.ecomuserservice.exception.*;
import com.pranshu.ecomuserservice.mapper.UserEntityDTOMapper;
import com.pranshu.ecomuserservice.model.Session;
import com.pranshu.ecomuserservice.model.SessionStatus;
import com.pranshu.ecomuserservice.model.User;
import com.pranshu.ecomuserservice.repository.SessionRepository;
import com.pranshu.ecomuserservice.repository.UserRepository;
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
        // Get user details from DB
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User for the given email id does not exist");
        }
        User user = userOptional.get();

        // Verify the user password given at the time of login
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialException("Invalid Credentials");
        }

        // Token generation
        //String token = RandomStringUtils.randomAlphanumeric(30);
        MacAlgorithm alg = Jwts.SIG.HS256; // HS256 algo added for JWT
        SecretKey key = alg.key().build(); // generating the secret key

        // Start adding the claims
        Date expiryAt = java.sql.Date.valueOf(LocalDate.now().plusDays(3));
        Map<String, Object> jsonForJWT = new HashMap<>();
        //jsonForJWT.put("email", user.getEmail()); // username/emailId is more vulnerable data of user
        jsonForJWT.put("userId", user.getId());
        jsonForJWT.put("roles", user.getRoles());
        jsonForJWT.put("createdAt", new Date());
        jsonForJWT.put("expiryAt", expiryAt);

        String token = Jwts.builder()
                .claims(jsonForJWT) // added the claims
                .signWith(key, alg) // added the algo and key
                .compact();         // building the token

        // Session creation
        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);
        session.setLoginAt(new Date());
        session.setExpiringAt(expiryAt);
        sessionRepository.save(session);

        // Generating the response
        UserDto userDto = UserEntityDTOMapper.getUserDTOFromUserEntity(user);

        // Setting up the headers
        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, token);
        return new ResponseEntity<>(userDto, headers, HttpStatus.OK);
    }

    public ResponseEntity<Void> logout(String token, Long userId) {
        // validations -> token exists, token is not expired, user exists else throw an exception
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);
        if (sessionOptional.isEmpty()) {
            throw new InvalidSessionException("session is invalid");
        }
        Session session = sessionOptional.get();
        session.setSessionStatus(SessionStatus.ENDED);
        sessionRepository.save(session);
        return ResponseEntity.ok().build();
    }

    public UserDto signUp(String email, String phoneNumber, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        User savedUser = userRepository.save(user);
        return UserDto.from(savedUser);
    }

    public SessionStatus validate(String token, Long userId) {
        // Check token expiry
        if(isTokenExpired(token)) {
            throw new TokenExpiredException("Token has expired");
        }

        // Verifying from DB if session exists
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);
        if (sessionOptional.isEmpty() || sessionOptional.get().getSessionStatus().equals(SessionStatus.ENDED)) {
            throw new InvalidTokenException("token is invalid");
        }
        return SessionStatus.ACTIVE;
    }

    /**
     * Parse the encoded JWT token, decode payload part to read the "expiryAt" claim.
     * If the expiryAt is before the current date, then the token is expired.
     * @param token
     * @return true if token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        String[] chunks = token.split("\\."); //token=header.payload.signature
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        ObjectMapper mapper = new ObjectMapper();
        JwtPayloadDTO jwtPayload = null;
        try {
            jwtPayload = mapper.readValue(payload, JwtPayloadDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Date expiryAt = new Date(jwtPayload.getExpiryAt());
        return expiryAt.before(new Date());
    }

    public ResponseEntity<List<Session>> getAllSession(){
        List<Session> sessions = sessionRepository.findAll();
        return ResponseEntity.ok(sessions);
    }

    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(userRepository.findAll());
    }

}
