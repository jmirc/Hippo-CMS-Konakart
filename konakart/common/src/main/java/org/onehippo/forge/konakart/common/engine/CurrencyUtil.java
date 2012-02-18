package org.onehippo.forge.konakart.common.engine;

import com.konakart.appif.CurrencyIf;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtil {


    /**
     * Returns the currency's formatter associated to the currency
     * @param currency The currency
     * @return the currency's formatter
     */
    public static DecimalFormat getFormatter(CurrencyIf currency) {
        DecimalFormatSymbols localDecimalFormatSymbols = new DecimalFormatSymbols();
        char decimalPoint;

        if ((currency.getDecimalPoint() != null) && (currency.getDecimalPoint().length() > 0)) {
            decimalPoint = currency.getDecimalPoint().charAt(0);
            localDecimalFormatSymbols.setDecimalSeparator(decimalPoint);
        }

        if ((currency.getThousandsPoint() != null) && (currency.getThousandsPoint().length() > 0)) {
            decimalPoint = currency.getThousandsPoint().charAt(0);
            localDecimalFormatSymbols.setGroupingSeparator(decimalPoint);
        }

        NumberFormat localNumberFormat = NumberFormat.getNumberInstance(Locale.US);
        DecimalFormat localDecimalFormat = (DecimalFormat) localNumberFormat;
        localDecimalFormat.setDecimalFormatSymbols(localDecimalFormatSymbols);
        localDecimalFormat.setMaximumFractionDigits(Integer.parseInt(currency.getDecimalPlaces()));
        localDecimalFormat.setMinimumFractionDigits(Integer.parseInt(currency.getDecimalPlaces()));

        return localDecimalFormat;
    }
}
