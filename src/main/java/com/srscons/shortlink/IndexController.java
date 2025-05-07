package com.srscons.shortlink;

import com.srscons.shortlink.auth.entity.UserEntity;
import com.srscons.shortlink.auth.util.CONSTANTS;
import com.srscons.shortlink.linkinbio.dto.LinkInBioDto;
import com.srscons.shortlink.linkinbio.entity.ThemeType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class IndexController {

    @RequestMapping(value={"/index", "/"})
    public String indexPage(HttpServletRequest request) {
        UserEntity loggedInUser = (UserEntity) request.getAttribute(CONSTANTS.LOGGED_IN_USER);
        if(loggedInUser != null) {
            return "redirect:/dashboard";
        }
        return "index";
    }

/*    @RequestMapping("/dashboard")
    public String dashboardPage() {
        return "dashboard";
    }*/

    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        LinkInBioDto link = new LinkInBioDto();
        link.setThemeType(ThemeType.AUTO);
        model.addAttribute("link", link);
        model.addAttribute("themeTypes", ThemeType.values());
        return "dashboard";
    }

}
