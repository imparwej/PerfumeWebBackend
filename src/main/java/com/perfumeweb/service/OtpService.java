package com.perfumeweb.service;

import com.perfumeweb.model.PasswordResetOTP;
import com.perfumeweb.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpRepository repo;

    public String createOtp(String email) {

        // random 6 digit OTP
        int otpNum = 100000 + new Random().nextInt(900000);
        String otp = String.valueOf(otpNum);

        PasswordResetOTP e = new PasswordResetOTP();
        e.setEmail(email);
        e.setOtp(otp);
        e.setExpiryTime(System.currentTimeMillis() + 300000); // 5 min

        repo.save(e);

        return otp;
    }

    public boolean verify(String email, String otp) {
        return repo.findByEmail(email)
                .filter(o ->
                        o.getOtp().equals(otp) &&
                                o.getExpiryTime() > System.currentTimeMillis()
                )
                .isPresent();
    }
}
