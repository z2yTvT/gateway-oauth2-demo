package com.z.oauth2.service;


import com.z.oauth2.pojo.User;
import com.z.oauth2.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;



@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!StringUtils.hasLength(username)){
            throw new RuntimeException("用户名不能为空");
        }
        //test
        //test11
        /////
        //pppp

        //==
        User user = userRepository.findByUsername(username);
        if(user != null){
            return new org.springframework.security.core.userdetails.User(
                username,user.getPassword(), AuthorityUtils.createAuthorityList(user.getPassword())
            );
        }
        throw new UsernameNotFoundException("user not found!");
    }
}
