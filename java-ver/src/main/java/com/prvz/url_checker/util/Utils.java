package com.prvz.url_checker.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {

    public static boolean isUrl(String url) {
        return url.matches("^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$");
    }

}
