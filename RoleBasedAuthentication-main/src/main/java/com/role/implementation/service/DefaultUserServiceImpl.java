package com.role.implementation.service;

import java.util.*;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.role.implementation.DTO.FarmerDetailsDTO;
import com.role.implementation.DTO.UserRegisteredDTO;
import com.role.implementation.model.Role;
import com.role.implementation.model.User;
import com.role.implementation.repository.RoleRepository;
import com.role.implementation.repository.UserRepository;

@Service
public class DefaultUserServiceImpl implements DefaultUserService{
   @Autowired
	private UserRepository userRepo;
   @Autowired
    private RoleRepository roleRepo;
   
   
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();



	@Override
	public List<User> getUsersByRole(int  role) {
		return userRepo.findByRole(role);
	}
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	
		User user = userRepo.findByEmail(email);
		if(user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), mapRolesToAuthorities(user.getRole()));		
	}
	
	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles){
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRole())).collect(Collectors.toList());
	}

	@Override
	public User save(UserRegisteredDTO userRegisteredDTO) {
		Role role = new Role();
		if(userRegisteredDTO.getRole().equals("USER"))
		  role = roleRepo.findByRole("USER");
		else if(userRegisteredDTO.getRole().equals("ADMIN"))
		 role = roleRepo.findByRole("ADMIN");
		User user = new User();
		user.setEmail(userRegisteredDTO.getEmail_id());
		user.setName(userRegisteredDTO.getName());
		user.setLocation(userRegisteredDTO.getLocation());
		user.setMobileNo(userRegisteredDTO.getMobile_no());
		user.setPassword(passwordEncoder.encode(userRegisteredDTO.getPassword()));
		user.setRole(role);
		return userRepo.save(user);
	}
	@Override
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

	@Override
	public void updateUser(User user) {
		userRepo.save(user);
	}
}
