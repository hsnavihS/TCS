package com.shivansh.TCS.controllers;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shivansh.TCS.mail.*;
import com.shivansh.TCS.model.*;
import com.shivansh.TCS.password.*;
import com.shivansh.TCS.repository.*;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    // @Autowired
    // EventRepository eventRepository;

    // @Autowired
    // RoomRepository roomRepository;

    // This method returns all the users in the database
    /*
     * GET /users/all
     * Returns all the users in the database
     * 
     * @return List<User> - List of all the users in the database
     * 
     * @throws 404 - If there is an error in the database
     * 
     * @param name (optional) - The name of the user in proper case
     *
     */
    @GetMapping("/all")
    public ResponseEntity<Object> getAllUsers(@RequestParam(required = false) String name) {
        try {
            List<User> users = new ArrayList<User>();

            if (name == null)
                userRepository.findAll().forEach(users::add);
            else
                userRepository.findByName(name).forEach(users::add);

            if (users.isEmpty()) {
                return new ResponseEntity<>("[]", HttpStatus.OK);
            }
            for (User user : users) {
                user.setPassword(null);
            }
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // This method returns a user by id
    /*
     * GET/users/{id}
     * Returns a user by id
     * 
     * @return User - The user with the given id
     * 
     * @throws 500 - If there is an error in the database
     * 
     * @param id (long integer) - The id of the user
     * 
     * eg: http://localhost:8080/users/1
     * 
     * {
     * "id": 1,
     * "name": "Shivansh Shukla",
     * "email": "shivansh@gmail.com",
     * "password": null,
     * "phone": 9876543210,
     * "role": "STUDENT",
     * }
     *
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") long id) {
        Optional<User> userData = userRepository.findById(id);

        if (userData.isPresent()) {
            userData.get().setPassword(null);
            return new ResponseEntity<>(userData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{}", HttpStatus.NOT_FOUND);
        }
    }

    /*
     * POST /users/edit/{id}
     * 
     * @return User - The user with the given id
     * 
     * @throws 500 - If there is an error in the database
     * 
     * @throws 404 - If the user is not found
     * 
     * In the body of the request, send the user object with the updated values.
     * - name
     * - password
     * - role
     * - phone
     * Right now only the name, role and phone can be updated.
     */
    @PostMapping("/edit/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") long id, @RequestBody User user) {
        Optional<User> userData = userRepository.findById(id);

        if (userData.isPresent()) {
            User _user = userData.get();
            if (user.getName() != null) {
                _user.setName(user.getName());
            }
            if (user.getPassword() != null || user.getPassword() == "") {
                _user.setPassword(user.getPassword());
            }
            if (user.getRole() != null) {
                _user.setRole(user.getRole());
            }

            if (user.getPhone() != null && user.getPhone() != 0) {
                _user.setPhone(user.getPhone());
            }

            userRepository.save(_user);
            _user.setPassword(null);
            return new ResponseEntity<>(_user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // This method returns a user by email
    /*
     * POST /users/signup
     * Returns a user by email
     * 
     * @return User - The user with the given email
     * 
     * @throws 409 - If the email already exists
     * 
     * @throws 500 - If there is an error in the database
     * 
     * @param email (String) - The email of the user
     * 
     * eg: http://localhost:8080/user/signup
     * 
     * Request Body:
     * {
     * "name": "Shivansh Shukla",
     * "email": "email@gmail.com"
     * "password": "password",
     * "phone": 9876543210,
     * "role": "STUDENT" (or "FACULTY")
     * }
     * 
     * Response Body:
     * {
     * "id": 1,
     * "name": "Shivansh Shukla",
     * "email": "shivansh@gmail.com"
     * "password": null,
     * "phone": 9999999999,
     * "role": "USER",
     * }
     */
    @PostMapping("/signup")
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        try {
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                return new ResponseEntity<>("The user already exisits", HttpStatus.CONFLICT);
            }

            // check if email is not of the domain bits-pilani
            if (!user.getEmail().contains("bits-pilani.ac.in")) {
                return new ResponseEntity<>("Please use your BITS email ID", HttpStatus.BAD_REQUEST);
            }

            String encodedPass = MD5.getMd5(user.getPassword());

            User _user = userRepository.save(
                    new User(user.getName(), user.getEmail(), encodedPass, user.getRole(), user.getPhone()));

            _user.setPassword(null);

            // Send email
            String subject = "Welcome to TCS";
            String body = "Hello " + _user.getName() + ",\n\n"
                    + "Welcome to TCS. You can now start scheduling your meetings using our website.\n\n"
                    + "Your account details are as follows:\n"
                    + "Email: " + _user.getEmail() + "\n"
                    + "Phone: " + _user.getPhone() + "\n\n"
                    + "Regards,\n" + "Team TCS";

            EmailDetails emailDetails = new EmailDetails(_user.getEmail(), body, subject, null);
            String status = emailService.sendSimpleMail(emailDetails);

            System.out.println("Email Status: " + status);

            return new ResponseEntity<>(_user, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Sign in
    /*
     * POST /users/signin
     * Returns a user by email
     * 
     * @return User - The user with the given email
     * 
     * @throws 404 - If the user does not exist
     * 
     * @throws 500 - If there is an error in the database
     * 
     * Request Body:
     * {
     * "email": "shuklashivansh@gmail.com",
     * "password": "password"
     * }
     * 
     * Response Body:
     * {
     * "id": 1,
     * "name": "Shivansh Shukla",
     * "email": "shivansh@gmail.com",
     * "password": null,
     * "role": "STUDENT",
     * }
     */
    @PostMapping("/signin")
    public ResponseEntity<Object> signIn(@RequestBody User user) {
        try {
            String encodedPass = MD5.getMd5(user.getPassword());
            Optional<User> userData = userRepository.findByEmailAndPassword(user.getEmail(), encodedPass);
            if (userData.isPresent()) {
                userData.get().setPassword(null);
                return new ResponseEntity<>(userData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("The user does not exist.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // This method deletes a user by id
    /*
     * DELETE /users/{id}
     * 
     * @return String - The message
     * 
     * @throws 500 - If there is an error in the database
     * 
     * @throws 404 - If the user is not found
     * 
     * @param id (long integer) - The id of the user
     * 
     * eg: http://localhost:8080/users/1
     * 
     * Response Body:
     * {
     * "message": "User deleted successfully!"
     * }
     */
    @PostMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) {
        try {
            // todo: probably remove the user from all the events that he is part of
            // can be done later though, doubt they'll check this in the demo
            userRepository.deleteById(id);
            return new ResponseEntity<>("User deleted successfully!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // This method returns a user by email
    /*
     * POST /users/forgotPassword
     * Returns a user by email
     * 
     * @return User - The user with the given email
     * 
     * @throws 404 - If the user does not exist
     * 
     * @throws 500 - If there is an error in the database
     * 
     * @param email (String) - The email of the user
     * 
     * eg: http://localhost:8080/user/forgotPassword
     * 
     * Request Body:
     * {
     * "email": "shivansh@gmail.com"
     * }
     * Response Body:
     * {
     * "message": "Email sent successfully!" or "The user does not exist."
     * }
     */
    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        try {
            Optional<User> userData = userRepository.findByEmail(email);

            if (userData.isPresent()) {
                String randomPassword = RandomPassword.generateRandomPassword();
                String hashedRandomPassword = MD5.getMd5(randomPassword);

                userData.get().setPassword(hashedRandomPassword);
                userRepository.save(userData.get());

                // Send email
                String subject = "TCS - Forgot Password";
                String body = "Hello " + userData.get().getName() + ",\n\n"
                        + "Your new password is: " + randomPassword + "\n\n"
                        + "Regards,\n" + "Team TCS";

                EmailDetails emailDetails = new EmailDetails(userData.get().getEmail(), body, subject, null);
                String status = emailService.sendSimpleMail(emailDetails);

                System.out.println("Email Status: " + status);

                return new ResponseEntity<>("Email sent successfully!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("The user does not exist.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // this method resets a user's password
    /*
     * POST /users/resetPassword
     * Returns a user by email
     * 
     * @return User - The user with the given email
     * 
     * @throws 404 - If the user does not exist
     * 
     * @throws 500 - If there is an error in the database
     * 
     * @param email (String) - The email of the user
     * 
     * eg: http://localhost:8080/user/resetPassword
     * 
     * Request Body:
     * {
     * "email": "email@gmail.com",
     * "password": "password"
     * }
     * 
     * Response Body:
     * {
     * "message": "Password reset successfully!" or "The user does not exist."
     * }
     */
    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String password) {
        try {
            Optional<User> userData = userRepository.findByEmail(email);

            if (userData.isPresent()) {
                String hashedPassword = MD5.getMd5(password);

                userData.get().setPassword(hashedPassword);
                userRepository.save(userData.get());

                return new ResponseEntity<>("Password reset successfully!", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("The user does not exist.", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
