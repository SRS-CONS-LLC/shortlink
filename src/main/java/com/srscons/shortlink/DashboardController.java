package com.srscons.shortlink;

import com.srscons.shortlink.linkinbio.controller.dto.response.LinkInBioResponseDto;
import com.srscons.shortlink.linkinbio.controller.mapper.LinkInBioViewMapper;
import com.srscons.shortlink.linkinbio.repository.entity.ThemeType;
import com.srscons.shortlink.linkinbio.service.LinkInBioService;
import com.srscons.shortlink.linkinbio.service.dto.LinkInBioDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
@AllArgsConstructor
public class DashboardController {

    private final LinkInBioService linkInBioService;
    private final LinkInBioViewMapper linkInBioViewMapper;

    @GetMapping
    public String indexPage(Model model) {
        LinkInBioDto linkInBioDto = linkInBioService.findById(1L);
        LinkInBioResponseDto response = linkInBioViewMapper.fromBusinessToResponse(linkInBioDto);

        model.addAttribute("link", response);
        model.addAttribute("themeTypes", ThemeType.values());

        return "dashboard";
    }

}
