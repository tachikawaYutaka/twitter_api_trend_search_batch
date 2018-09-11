package com.wakabatimes.twitter_api_trend_search_batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wakabatimes.twitter_api_trend_search_batch.entity.TwitterAccessParam;
import com.wakabatimes.twitter_api_trend_search_batch.service.FileParseService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.io.*;
import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootApplication
public class TwitterApiTrendSearchBatchApplication  implements CommandLineRunner {
    @Autowired
    private FileParseService fileParseService;

    private static final Logger logger = LoggerFactory.getLogger(TwitterApiTrendSearchBatchApplication.class);

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(TwitterApiTrendSearchBatchApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        ApplicationContext context = application.run(args);
        SpringApplication.exit(context);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("- - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
        logger.info("the start of twitter api trend search batch");
        List<String> argsList = Arrays.asList(args);
        if (argsList.size() == 3) {
            TwitterAccessParam twitterAccessParam = fileParseService.getParam(argsList.get(0));
            String twitterConsumerKey    = twitterAccessParam.getTwitterConsumerKey();
            String twitterConsumerSecret = twitterAccessParam.getTwitterConsumerSecret();
            String twitterAccessToken        = twitterAccessParam.getTwitterAccessToken();
            String twitterAccessTokenSecret = twitterAccessParam.getTwitterAccessTokenSecret();
            // アクセストークンの設定
            AccessToken token = new AccessToken(twitterAccessToken, twitterAccessTokenSecret);

            Twitter twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer(twitterConsumerKey, twitterConsumerSecret);
            twitter.setOAuthAccessToken(token);

            // 表示してみる。
            Trends trends =  twitter.getPlaceTrends(twitterAccessParam.getTrendWoeId());

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(trends);

            //ファイル作成
            String dir = argsList.get(1);
            String fileName = argsList.get(2);

            File dirPath = new File(dir);
            if(!dirPath.exists()) {
                if(dirPath.mkdirs()){
                    logger.info("create dir");
                }
            }

            String filePath = dir + File.separator + fileName;
            File newFile = new File(filePath);
            if(newFile.createNewFile()) {
                logger.info("create file");
            }

            if(checkBeforeWriteFile(newFile)) {
                // 文字コードを指定する
                PrintWriter p_writer = null;
                try {
                    p_writer = new PrintWriter(new BufferedWriter
                            (new OutputStreamWriter(new FileOutputStream(newFile),"UTF-8")));
                } catch (UnsupportedEncodingException | FileNotFoundException e) {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                }
                if (p_writer != null) {
                    p_writer.println(json);
                    p_writer.close();
                }
                logger.info("Create " + filePath);
            }else {
                logger.error("Error Can not write the file");
            }
        }else {
            logger.error("Incorrect params");
        }
    }

    private static boolean checkBeforeWriteFile(File file) {
        return file.exists() && !(!file.isFile() || !file.canWrite());
    }
}
