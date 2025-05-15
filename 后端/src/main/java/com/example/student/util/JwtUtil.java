package com.example.student.util;

import com.example.student.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT工具类
 *
 * @author example
 */
@Component
public class JwtUtil {

    @Resource
    private JwtConfig jwtConfig;

    /**
     * 生成JWT密钥
     *
     * @return 密钥
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 从JWT中获取用户名
     *
     * @param token JWT
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从JWT中获取过期时间
     *
     * @param token JWT
     * @return 过期时间
     */
    public Date getExpirationFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从JWT中获取指定声明
     *
     * @param token          JWT
     * @param claimsResolver 声明解析函数
     * @param <T>            返回类型
     * @return 声明
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从JWT中获取所有声明
     *
     * @param token JWT
     * @return 所有声明
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 检查JWT是否过期
     *
     * @param token JWT
     * @return 是否过期
     */
    private Boolean isTokenExpired(String token) {
        Date expiration = getExpirationFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 生成JWT
     *
     * @param userDetails 用户详情
     * @return JWT
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(2);
        return generateToken(claims, userDetails.getUsername());
    }

    /**
     * 生成JWT
     *
     * @param claims   声明
     * @param subject  主题（用户名）
     * @return JWT
     */
    private String generateToken(Map<String, Object> claims, String subject) {
        // 过期时间
        long expiration = jwtConfig.getExpire() * 1000L;
        Date expirationDate = new Date(System.currentTimeMillis() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 验证JWT是否有效
     *
     * @param token       JWT
     * @param userDetails 用户详情
     * @return 是否有效
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
} 