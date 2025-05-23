package com.srscons.shortlink.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtils {
    // Video Platforms
    private static final Pattern YOUTUBE_PATTERN = Pattern.compile(
        "(?:youtube\\.com\\/(?:watch\\?v=|embed\\/|v\\/)|youtu\\.be\\/)([\\w-]{11})"
    );
    private static final Pattern TIKTOK_PATTERN = Pattern.compile(
        "(?:tiktok\\.com\\/@[\\w.-]+\\/video\\/|vm\\.tiktok\\.com\\/)([\\d]+)"
    );
    private static final Pattern VIMEO_PATTERN = Pattern.compile(
        "(?:vimeo\\.com\\/)([\\d]+)"
    );
    private static final Pattern TWITCH_PATTERN = Pattern.compile(
        "(?:twitch\\.tv\\/(?:videos\\/|(?:[\\w-]+)))([\\d]+)?"
    );
    private static final Pattern DAILYMOTION_PATTERN = Pattern.compile(
        "(?:dailymotion\\.com\\/video\\/)([\\w-]+)"
    );

    // Social Media Platforms
    private static final Pattern FACEBOOK_PATTERN = Pattern.compile(
        "(?:facebook\\.com\\/(?:profile\\.php\\?id=|(?:[\\w.-]+)\\/posts\\/|(?:[\\w.-]+)\\/photos\\/|(?:[\\w.-]+)\\/videos\\/|(?:[\\w.-]+)\\/))([\\d]+)?"
    );
    private static final Pattern TWITTER_PATTERN = Pattern.compile(
        "(?:twitter\\.com|x\\.com)\\/(?:[\\w.-]+)\\/status\\/([\\d]+)"
    );
    private static final Pattern INSTAGRAM_PATTERN = Pattern.compile(
        "(?:instagram\\.com\\/(?:p|reel)\\/([\\w-]+)|instagr\\.am\\/(?:p|reel)\\/([\\w-]+))"
    );
    private static final Pattern LINKEDIN_PATTERN = Pattern.compile(
        "(?:linkedin\\.com\\/(?:in|company|post)\\/([\\w-]+))"
    );
    private static final Pattern PINTEREST_PATTERN = Pattern.compile(
        "(?:pinterest\\.com\\/pin\\/([\\d]+))"
    );
    private static final Pattern SNAPCHAT_PATTERN = Pattern.compile(
        "(?:snapchat\\.com\\/add\\/([\\w.-]+))"
    );

    // Music Platforms
    private static final Pattern SPOTIFY_PATTERN = Pattern.compile(
        "(?:open\\.spotify\\.com\\/(?:track|album|artist|playlist)\\/([\\w-]+))"
    );
    private static final Pattern APPLE_MUSIC_PATTERN = Pattern.compile(
        "(?:music\\.apple\\.com\\/(?:[a-z]{2}\\/)?(?:album|artist|playlist)\\/(?:[\\w-]+)\\/([\\d]+))"
    );
    private static final Pattern SOUNDCLOUD_PATTERN = Pattern.compile(
        "(?:soundcloud\\.com\\/(?:[\\w.-]+)\\/[\\w-]+)"
    );

    // Shopping Platforms
    private static final Pattern AMAZON_PATTERN = Pattern.compile(
        "(?:amazon\\.(?:com|co\\.uk|de|fr|it|es|ca|co\\.jp|in|com\\.mx|com\\.br|com\\.au)\\/(?:dp|gp\\/product)\\/([A-Z0-9]{10}))"
    );
    private static final Pattern EBAY_PATTERN = Pattern.compile(
        "(?:ebay\\.(?:com|co\\.uk|de|fr|it|es|ca|com\\.au)\\/(?:itm|p)\\/([\\d]+))"
    );
    private static final Pattern ALIEXPRESS_PATTERN = Pattern.compile(
        "(?:aliexpress\\.com\\/item\\/([\\d]+))"
    );

    // Food Delivery Platforms
    private static final Pattern UBER_EATS_PATTERN = Pattern.compile(
        "(?:ubereats\\.com\\/(?:[a-z]{2}\\/)?store\\/([\\w-]+))"
    );
    private static final Pattern DOORDASH_PATTERN = Pattern.compile(
        "(?:doordash\\.com\\/store\\/([\\w-]+))"
    );
    private static final Pattern GRUBHUB_PATTERN = Pattern.compile(
        "(?:grubhub\\.com\\/restaurant\\/([\\w-]+))"
    );

    public static String getDeepLinkUrl(String url) {
        if (url == null) {
            return null;
        }

        // Video Platforms
        Matcher youtubeMatcher = YOUTUBE_PATTERN.matcher(url);
        if (youtubeMatcher.find()) {
            return "vnd.youtube://" + youtubeMatcher.group(1);
        }

        Matcher tiktokMatcher = TIKTOK_PATTERN.matcher(url);
        if (tiktokMatcher.find()) {
            return "snssdk1233://video/" + tiktokMatcher.group(1);
        }

        Matcher vimeoMatcher = VIMEO_PATTERN.matcher(url);
        if (vimeoMatcher.find()) {
            return "vimeo://app.vimeo.com/videos/" + vimeoMatcher.group(1);
        }

        Matcher twitchMatcher = TWITCH_PATTERN.matcher(url);
        if (twitchMatcher.find()) {
            String channel = twitchMatcher.group(1);
            return channel != null ? "twitch://stream/" + channel : "twitch://channel/" + twitchMatcher.group(0);
        }

        Matcher dailymotionMatcher = DAILYMOTION_PATTERN.matcher(url);
        if (dailymotionMatcher.find()) {
            return "dailymotion://video/" + dailymotionMatcher.group(1);
        }

        // Social Media Platforms
        Matcher facebookMatcher = FACEBOOK_PATTERN.matcher(url);
        if (facebookMatcher.find()) {
            String id = facebookMatcher.group(1);
            return id != null ? "fb://profile/" + id : "fb://page/" + facebookMatcher.group(0);
        }

        Matcher twitterMatcher = TWITTER_PATTERN.matcher(url);
        if (twitterMatcher.find()) {
            return "twitter://status?id=" + twitterMatcher.group(1);
        }

        Matcher instagramMatcher = INSTAGRAM_PATTERN.matcher(url);
        if (instagramMatcher.find()) {
            String postId = instagramMatcher.group(1) != null ? instagramMatcher.group(1) : instagramMatcher.group(2);
            return "instagram://media?id=" + postId;
        }

        Matcher linkedinMatcher = LINKEDIN_PATTERN.matcher(url);
        if (linkedinMatcher.find()) {
            String type = url.contains("/in/") ? "profile" : url.contains("/company/") ? "company" : "post";
            return "linkedin://" + type + "/" + linkedinMatcher.group(1);
        }

        Matcher pinterestMatcher = PINTEREST_PATTERN.matcher(url);
        if (pinterestMatcher.find()) {
            return "pinterest://pin/" + pinterestMatcher.group(1);
        }

        Matcher snapchatMatcher = SNAPCHAT_PATTERN.matcher(url);
        if (snapchatMatcher.find()) {
            return "snapchat://add/" + snapchatMatcher.group(1);
        }

        // Music Platforms
        Matcher spotifyMatcher = SPOTIFY_PATTERN.matcher(url);
        if (spotifyMatcher.find()) {
            String type = url.contains("/track/") ? "track" : 
                         url.contains("/album/") ? "album" : 
                         url.contains("/artist/") ? "artist" : "playlist";
            return "spotify://" + type + "/" + spotifyMatcher.group(1);
        }

        Matcher appleMusicMatcher = APPLE_MUSIC_PATTERN.matcher(url);
        if (appleMusicMatcher.find()) {
            String type = url.contains("/album/") ? "album" : 
                         url.contains("/artist/") ? "artist" : "playlist";
            return "music://" + type + "/" + appleMusicMatcher.group(1);
        }

        Matcher soundcloudMatcher = SOUNDCLOUD_PATTERN.matcher(url);
        if (soundcloudMatcher.find()) {
            return "soundcloud://" + soundcloudMatcher.group(0);
        }

        // Shopping Platforms
        Matcher amazonMatcher = AMAZON_PATTERN.matcher(url);
        if (amazonMatcher.find()) {
            return "amzn://dp/" + amazonMatcher.group(1);
        }

        Matcher ebayMatcher = EBAY_PATTERN.matcher(url);
        if (ebayMatcher.find()) {
            return "ebay://item/" + ebayMatcher.group(1);
        }

        Matcher aliexpressMatcher = ALIEXPRESS_PATTERN.matcher(url);
        if (aliexpressMatcher.find()) {
            return "aliexpress://item/" + aliexpressMatcher.group(1);
        }

        // Food Delivery Platforms
        Matcher uberEatsMatcher = UBER_EATS_PATTERN.matcher(url);
        if (uberEatsMatcher.find()) {
            return "ubereats://store/" + uberEatsMatcher.group(1);
        }

        Matcher doordashMatcher = DOORDASH_PATTERN.matcher(url);
        if (doordashMatcher.find()) {
            return "doordash://store/" + doordashMatcher.group(1);
        }

        Matcher grubhubMatcher = GRUBHUB_PATTERN.matcher(url);
        if (grubhubMatcher.find()) {
            return "grubhub://restaurant/" + grubhubMatcher.group(1);
        }

        // Return original URL for unsupported URLs
        return url;
    }

    public static String getFallbackUrl(String url) {
        if (url == null) {
            return null;
        }

        // For all platforms, we'll use the original URL as fallback
        // This is because most platforms don't have a reliable way to convert IDs back to URLs
        // and the original URL is already in the correct format for web viewing
        return url;
    }

    public static boolean isAppDeepLinkable(String url) {
        if (url == null) {
            return false;
        }
        return isYouTubeUrl(url) || isTikTokUrl(url) || isVimeoUrl(url) || isTwitchUrl(url) ||
               isDailymotionUrl(url) || isFacebookUrl(url) || isTwitterUrl(url) || isInstagramUrl(url) ||
               isLinkedInUrl(url) || isPinterestUrl(url) || isSnapchatUrl(url) || isSpotifyUrl(url) ||
               isAppleMusicUrl(url) || isSoundCloudUrl(url) || isAmazonUrl(url) || isEbayUrl(url) ||
               isAliExpressUrl(url) || isUberEatsUrl(url) || isDoorDashUrl(url) || isGrubHubUrl(url);
    }

    // Video Platform Checks
    public static boolean isYouTubeUrl(String url) {
        return url != null && YOUTUBE_PATTERN.matcher(url).find();
    }

    public static boolean isTikTokUrl(String url) {
        return url != null && TIKTOK_PATTERN.matcher(url).find();
    }

    public static boolean isVimeoUrl(String url) {
        return url != null && VIMEO_PATTERN.matcher(url).find();
    }

    public static boolean isTwitchUrl(String url) {
        return url != null && TWITCH_PATTERN.matcher(url).find();
    }

    public static boolean isDailymotionUrl(String url) {
        return url != null && DAILYMOTION_PATTERN.matcher(url).find();
    }

    // Social Media Platform Checks
    public static boolean isFacebookUrl(String url) {
        return url != null && FACEBOOK_PATTERN.matcher(url).find();
    }

    public static boolean isTwitterUrl(String url) {
        return url != null && TWITTER_PATTERN.matcher(url).find();
    }

    public static boolean isInstagramUrl(String url) {
        return url != null && INSTAGRAM_PATTERN.matcher(url).find();
    }

    public static boolean isLinkedInUrl(String url) {
        return url != null && LINKEDIN_PATTERN.matcher(url).find();
    }

    public static boolean isPinterestUrl(String url) {
        return url != null && PINTEREST_PATTERN.matcher(url).find();
    }

    public static boolean isSnapchatUrl(String url) {
        return url != null && SNAPCHAT_PATTERN.matcher(url).find();
    }

    // Music Platform Checks
    public static boolean isSpotifyUrl(String url) {
        return url != null && SPOTIFY_PATTERN.matcher(url).find();
    }

    public static boolean isAppleMusicUrl(String url) {
        return url != null && APPLE_MUSIC_PATTERN.matcher(url).find();
    }

    public static boolean isSoundCloudUrl(String url) {
        return url != null && SOUNDCLOUD_PATTERN.matcher(url).find();
    }

    // Shopping Platform Checks
    public static boolean isAmazonUrl(String url) {
        return url != null && AMAZON_PATTERN.matcher(url).find();
    }

    public static boolean isEbayUrl(String url) {
        return url != null && EBAY_PATTERN.matcher(url).find();
    }

    public static boolean isAliExpressUrl(String url) {
        return url != null && ALIEXPRESS_PATTERN.matcher(url).find();
    }

    // Food Delivery Platform Checks
    public static boolean isUberEatsUrl(String url) {
        return url != null && UBER_EATS_PATTERN.matcher(url).find();
    }

    public static boolean isDoorDashUrl(String url) {
        return url != null && DOORDASH_PATTERN.matcher(url).find();
    }

    public static boolean isGrubHubUrl(String url) {
        return url != null && GRUBHUB_PATTERN.matcher(url).find();
    }

    public static String getAppName(String url) {
        if (isYouTubeUrl(url)) return "YouTube";
        if (isTikTokUrl(url)) return "TikTok";
        if (isVimeoUrl(url)) return "Vimeo";
        if (isTwitchUrl(url)) return "Twitch";
        if (isDailymotionUrl(url)) return "Dailymotion";
        if (isFacebookUrl(url)) return "Facebook";
        if (isTwitterUrl(url)) return "Twitter";
        if (isInstagramUrl(url)) return "Instagram";
        if (isLinkedInUrl(url)) return "LinkedIn";
        if (isPinterestUrl(url)) return "Pinterest";
        if (isSnapchatUrl(url)) return "Snapchat";
        if (isSpotifyUrl(url)) return "Spotify";
        if (isAppleMusicUrl(url)) return "Apple Music";
        if (isSoundCloudUrl(url)) return "SoundCloud";
        if (isAmazonUrl(url)) return "Amazon";
        if (isEbayUrl(url)) return "eBay";
        if (isAliExpressUrl(url)) return "AliExpress";
        if (isUberEatsUrl(url)) return "Uber Eats";
        if (isDoorDashUrl(url)) return "DoorDash";
        if (isGrubHubUrl(url)) return "GrubHub";
        return null;
    }
} 