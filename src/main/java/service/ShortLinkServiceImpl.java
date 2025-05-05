package service;

import com.srscons.shortlink.model.ShortLink;
import com.srscons.shortlink.repository.ShortLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShortLinkServiceImpl implements ShortLinkService {

    @Autowired
    private ShortLinkRepository shortLinkRepository;

    @Override
    public String getOriginalUrlByCode(String code) {
        ShortLink shortLink = shortLinkRepository.findByCode(code);
        return shortLink != null ? shortLink.getOriginalUrl() : null;
    }
}
