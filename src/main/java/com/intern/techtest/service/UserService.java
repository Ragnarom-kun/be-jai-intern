package com.intern.techtest.service;

import com.intern.techtest.model.User;
import com.intern.techtest.payload.request.SignupRequest;

public interface UserService {
    User getUserByUsername(String username);

    Boolean cekExistsUserByUsername(String username);

    void createUser(SignupRequest signUpRequest);

    void updateProfileUser(String username, String name);

    Boolean upgradeUserMembership(String username);

    Boolean cekUserConvertEligibility(String username, int totalConversion);
}
