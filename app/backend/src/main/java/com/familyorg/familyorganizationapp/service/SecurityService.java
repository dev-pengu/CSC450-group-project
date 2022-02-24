package com.familyorg.familyorganizationapp.service;

public interface SecurityService {
	boolean isAuthenticated();
	void autologin(String username, String password);
}
