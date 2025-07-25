package project.healthcare_appointment.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import project.healthcare_appointment.model.User;
import project.healthcare_appointment.repository.UserRepository;

import javax.crypto.spec.SecretKeySpec;

@Slf4j
@Component
public class JwtDecoderCustom implements JwtDecoder {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.signer-key}")
    private String signerKey;

    @Autowired
    private UserRepository userRepository;

    private NimbusJwtDecoder nimbusJwtDecoder = null;


    @Override
    public Jwt decode(String token) throws JwtException {
        try {

            var response = jwtUtil.verifyToken(token);

            if(!response) {
                throw new JwtException("Token verification failed ");
            }

            if (nimbusJwtDecoder == null) {
                SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
                nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                        .macAlgorithm(MacAlgorithm.HS512)
                        .build();
            }

            Jwt jwt = nimbusJwtDecoder.decode(token);

            String username = jwt.getSubject();
            if (username == null || username.isEmpty()) {
                throw new JwtException("Token missing subject claim");
            }

            User user = userRepository.findUserWithoutRelationships(username)
                    .orElseThrow(() -> new JwtException("User not found"));

            if (!user.getIsActive()) {
                throw new JwtException("User is not active");
            }

            log.debug("Token decoded successfully for user: {}", username);
            return jwt;

        } catch (JwtException e) {
           // log.error("JWT decoding failed: {}", e.getMessage());
            throw new JwtException("Authentication failed");
        } catch (Exception e) {
            //.error("Unexpected error during JWT decoding: {}", e.getMessage());
            throw new JwtException("Authentication failed: ");
        }
    }
}

