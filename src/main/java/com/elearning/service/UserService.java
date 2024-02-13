package com.elearning.service;

import com.elearning.entity.Course;
import com.elearning.entity.User;

public interface UserService {

	public User saveUser(User user,String url);

	public void removeSessionMessage();

	public void sendEmail(User user,String path);
	
	public boolean verifyAccount(String verificationCode);
	
	User userUpdate(int userId, String name, String phone,String email, String password);
	
	User resetPassword(String email, String password);
	
	Course courseUpdate(long id,String courseName, long coursePrice);
}
