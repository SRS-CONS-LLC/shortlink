package com.srscons.shortlink.linkinbio.controller;


import com.srscons.shortlink.linkinbio.dto.LinkInBioDto;
import com.srscons.shortlink.linkinbio.service.LinkInBioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/link-in-bio")
public class LinkInBioController {

    private final LinkInBioService linkInBioService;

    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        model.addAttribute("link", new LinkInBioDto());
        return "dashboard";
    }

    @PostMapping("/linkinbio")
    public String createLinkInBio(@ModelAttribute LinkInBioDto request, RedirectAttributes redirectAttributes) {
        LinkInBioDto response = linkInBioService.create(request);
        redirectAttributes.addFlashAttribute("link", response);
        return "redirect:/dashboard";
    }
}
