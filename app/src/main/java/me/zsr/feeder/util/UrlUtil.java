package me.zsr.feeder.util;

import org.apache.commons.validator.routines.UrlValidator;

/**
 * @description:
 * @author: Zhangshaoru
 * @date: 15-7-20
 */
public class UrlUtil {

    //TODO may be a async method
    public static String searchForTarget(String input) {
        String[] schemes = {"http", "https"};
        UrlValidator urlValidator = new UrlValidator(schemes);

        if (urlValidator.isValid(input)) {
            return input;
        }

        String inputWithPrefix = "http://" + input;
        if (urlValidator.isValid(inputWithPrefix)) {
            return inputWithPrefix;
        }

        return null;
    }
}
