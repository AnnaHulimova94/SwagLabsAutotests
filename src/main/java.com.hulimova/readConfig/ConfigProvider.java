package readConfig;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import entity.User;

import java.util.HashMap;
import java.util.Map;

public interface ConfigProvider {

    Config config = readConfig();

    String userStandard = config.getString("userParams.user_standard.status");

    String userLockedOut = config.getString("userParams.user_locked_out.status");

    String userProblem = config.getString("userParams.user_problem.status");

    String userPerformanceGlitch = config.getString("userParams.user_performance_glitch.status");

    String userError = config.getString("userParams.user_error.status");

    String userVisual = config.getString("userParams.user_visual.status");

    Map<String, User> userMap = getUserMap();

    static Config readConfig() {
        return ConfigFactory.systemProperties().hasPath("testProfile")
                ? ConfigFactory.load(ConfigFactory.systemProperties().getString("testProfile"))
                : ConfigFactory.load("application.conf");
    }

    static Map<String, User> getUserMap() {
        Map<String, User> userMap = new HashMap<>();

        ConfigObject configObject = readConfig().getObject("userParams");

        configObject.forEach((s, configValue) -> {
            String login = configValue.atKey("login").getObject("login").toConfig().getString("login");
            String password = configValue.atKey("password").getObject("password").toConfig().getString("password");
            userMap.put(s, new User(login, password));
        });


        return userMap;
    }
}
