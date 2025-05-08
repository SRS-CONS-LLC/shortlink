package com.srscons.shortlink.linkinbio.exception;

public class LinkInBioNotFoundException extends RuntimeException {

    public LinkInBioNotFoundException(Long id) {
        super("LinkInBio not found with id: " + id);
    }
}
