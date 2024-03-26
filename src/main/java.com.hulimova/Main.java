import readConfig.ConfigProvider;

import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Map uMap = ConfigProvider.userMap;
        System.out.println(ConfigProvider.userStandard);
    }
}
