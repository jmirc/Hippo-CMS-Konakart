package org.onehippo.cms7.hst.hippokart.gogreen.database.utils;

import com.konakartadmin.app.AdminLanguage;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class LanguageUtil {

    private static Map<String, String> mappingLocaleTranslations = new HashMap<String, String>();

    static {
        mappingLocaleTranslations.put("en", "en_US");
        mappingLocaleTranslations.put("fr", "fr_FR");
        mappingLocaleTranslations.put("nl", "nl_NL");
        mappingLocaleTranslations.put("it", "it_IT");
        mappingLocaleTranslations.put("de", "de_DE");
        mappingLocaleTranslations.put("zh", "cn_ZH");
        mappingLocaleTranslations.put("es", "es_ES");
        mappingLocaleTranslations.put("ru", "ru_RU");
    }


    public static String getMappingTranslations(String locale) {
        return mappingLocaleTranslations.get(locale);
    }

    public static int getLanguageId(String locale, AdminLanguage[] adminLanguages) {
        for (AdminLanguage adminLanguage : adminLanguages) {
            if (StringUtils.equalsIgnoreCase(locale, adminLanguage.getLocale())) {
                return adminLanguage.getId();
            }
        }

        throw new IllegalArgumentException("Failed to find a valid database locale for the locale named : " + locale);
    }

}
