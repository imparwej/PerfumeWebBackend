package com.perfumeweb.controller;

import com.perfumeweb.model.Address;
import com.perfumeweb.model.User;
import com.perfumeweb.repository.AddressRepository;
import com.perfumeweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    // 🔥 helper method to safely get logged-in user
    private User getCurrentUser(Principal principal) {

        if (principal == null) {
            throw new RuntimeException("User not authenticated");
        }

        String email = principal.getName();

        System.out.println("Authenticated user: " + email); // debug log

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping
    public Address saveAddress(@RequestBody Address address, Principal principal) {

        User user = getCurrentUser(principal);

        address.setUser(user);

        return addressRepository.save(address);
    }

    @GetMapping
    public List<Address> getAddresses(Principal principal) {

        User user = getCurrentUser(principal);

        return addressRepository.findByUser(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Principal principal) {

        getCurrentUser(principal); // ensure user logged in

        addressRepository.deleteById(id);
    }
    @PutMapping("/{id}")
    public Address updateAddress(
            @PathVariable Long id,
            @RequestBody Address updated,
            Principal principal
    ) {

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow();

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        // ownership check
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not allowed");
        }

        // 🔥 update ALL fields
        address.setType(updated.getType());
        address.setName(updated.getName());
        address.setPhone(updated.getPhone());
        address.setAltPhone(updated.getAltPhone());
        address.setLandmark(updated.getLandmark());
        address.setLine(updated.getLine());
        address.setCity(updated.getCity());
        address.setPostalCode(updated.getPostalCode());

        return addressRepository.save(address);
    }


}
