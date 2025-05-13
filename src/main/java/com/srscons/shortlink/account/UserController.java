package com.srscons.shortlink.account;

import com.srscons.shortlink.auth.entity.UserEntity;
import com.srscons.shortlink.auth.repository.UserRepository;
import com.srscons.shortlink.auth.util.CONSTANTS;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/account")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping
    public String accountPage() {
        return "account";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String updateAccount(UserEntity user, Model model, HttpServletRequest request) {
        UserEntity loggedInUser = (UserEntity) request.getAttribute(CONSTANTS.LOGGED_IN_USER);
        if (user.getFullName() != null && !user.getFullName().isBlank()) {
            loggedInUser.setFullName(user.getFullName());
        }
        loggedInUser.setPhone(user.getPhone());
        userRepository.save(loggedInUser);

        model.addAttribute("message", "Successfully updated!");

        return "account";
    }

}
