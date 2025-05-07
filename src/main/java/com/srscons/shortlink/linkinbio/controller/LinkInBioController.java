package com.srscons.shortlink.linkinbio.controller;

import com.srscons.shortlink.linkinbio.controller.dto.request.LinkInBioRequestDto;
import com.srscons.shortlink.linkinbio.controller.dto.response.LinkInBioResponseDto;
import com.srscons.shortlink.linkinbio.controller.mapper.LinkInBioViewMapper;
import com.srscons.shortlink.linkinbio.service.LinkInBioService;
import com.srscons.shortlink.linkinbio.service.dto.LinkInBioDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/link-in-bio")
public class LinkInBioController {

    private final LinkInBioService linkInBioService;

    private final LinkInBioViewMapper linkInBioViewMapper;

    @PostMapping("/upload")
    public String createLinkInBio(@ModelAttribute LinkInBioRequestDto request, RedirectAttributes redirectAttributes) {
        LinkInBioDto linkInBioDto = linkInBioViewMapper.fromRequestToBusiness(request);
        LinkInBioDto createdBioDto = linkInBioService.create(linkInBioDto);

        LinkInBioResponseDto responseDto = linkInBioViewMapper.fromBusinessToResponse(createdBioDto);
        redirectAttributes.addFlashAttribute("link", responseDto);

        return "redirect:/dashboard";
    }

}
