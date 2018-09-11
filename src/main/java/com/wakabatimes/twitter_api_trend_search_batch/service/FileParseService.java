package com.wakabatimes.twitter_api_trend_search_batch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wakabatimes.twitter_api_trend_search_batch.TwitterApiTrendSearchBatchApplication;
import com.wakabatimes.twitter_api_trend_search_batch.entity.TwitterAccessParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class FileParseService {
    private static final Logger logger = LoggerFactory.getLogger(TwitterApiTrendSearchBatchApplication.class);

    public TwitterAccessParam getParam(String path) {
        String data = null;
        try {
            data = readFile(path);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        ObjectMapper mapper = new ObjectMapper();
        TwitterAccessParam twitterAccessParam = null;
        try {
            twitterAccessParam = mapper.readValue(data, TwitterAccessParam.class);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return twitterAccessParam;
    }

    private String readFile(String path) {
        File file = new File(path);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        StringBuilder data = new StringBuilder();
        String str = null;
        try {
            if (br != null) {
                str = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        while(str != null){
            data.append(str);
            try {
                str = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        }

        try {
            if (br != null) {
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return data.toString();
    }
}
