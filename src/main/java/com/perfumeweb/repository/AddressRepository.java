package com.perfumeweb.repository;

import com.perfumeweb.model.Address;
import com.perfumeweb.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);
}
