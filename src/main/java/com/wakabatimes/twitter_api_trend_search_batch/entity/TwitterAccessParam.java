package com.wakabatimes.twitter_api_trend_search_batch.entity;

import lombok.Data;

@Data
public class TwitterAccessParam {
    private String twitterConsumerKey;
    private String twitterConsumerSecret;
    private String twitterAccessToken;
    private String twitterAccessTokenSecret;
    private Integer trendWoeId;
}
