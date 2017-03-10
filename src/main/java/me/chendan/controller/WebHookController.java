package me.chendan.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author dan E-mail:hi@chendan.me
 * @since 17-3-10
 */
@RestController
@RequestMapping("/githook")
public class WebHookController {
    @Value("${shell.dir}")
    private String DIR;

    @Value("${token}")
    private String TOKEN;
    private final Logger logger = LoggerFactory.getLogger(WebHookController.class);

    @PostMapping("")
    public Object flaskMqttCi(@RequestHeader(name = "X-Gitlab-Token") String token, @RequestBody String body) {
        Format format = new SimpleDateFormat("YYYY年MM月DD日 HH:mm:ss");
        logger.info("Date: " + format.format(new Date()));
        if (!token.equals(TOKEN)) {
            logger.error("Invalid token: " + token);
            logger.info("=================================================================");
            return HttpStatus.FORBIDDEN;
        }
        JSONObject json = JSON.parseObject(body);
        String branch = json.getString("ref");
        int totalCommit = json.getInteger("total_commits_count");
        if (!branch.equals("refs/heads/master") || totalCommit == 0) {
            logger.info("NOT MASTER BRANCH!!!!!!!!");
            logger.info("Push to branch: " + branch);
            logger.info("Total commits count: " + totalCommit);
            return json;
        }
        String projectName = json.getJSONObject("project").getString("name");
        String shellPath = DIR + projectName + ".sh";
        logger.info("Project: " + projectName);
        logger.info("User_name: " + json.getString("user_name"));
        logger.info("Script: " + shellPath);
        logger.info("Data Received: " + json.toString());
        try {
            Process ps = Runtime.getRuntime().exec(shellPath);
            ps.waitFor();
            logger.info("Shell exit value: " + ps.exitValue());
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return "error";
        }
        logger.info("=================================================================");
        return json;
    }
}
