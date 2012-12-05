package org.onehippo.forge.konakart.hst.utils;

import com.konakart.app.Address;
import com.konakart.app.CustomerRegistration;
import com.konakart.appif.AddressIf;
import com.konakart.appif.CustomerRegistrationIf;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.component.support.forms.FormField;
import org.hippoecm.hst.component.support.forms.FormMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class KKRegisterFormUtils {
  protected Logger log = LoggerFactory.getLogger(getClass());

  public static final String MALE_GENDER = "m";
  public static final String FEMALE_GENDER = "f";
  public static final String DEFAULT_GENDER = MALE_GENDER;

  public static final String COUNTRIES = "countries";
  public static final String PROVINCES = "provinces";
  public static final String ADDRESSES = "addresses";


  public static final String EMAIL = "email";
  public static final String DATEOFBIRTH = "dateofbirth";
  public static final String ADDRESS = "address";
  public static final String GENDER = "gender";
  public static final String FIRSTNAME = "firstname";
  public static final String LASTNAME = "lastname";
  public static final String COMPANYNAME = "companyname";
  public static final String STREETADDRESS = "streetaddress";
  public static final String SUBURB = "suburb";
  public static final String POSTALCODE = "postalcode";
  public static final String CITY = "city";
  public static final String STATEPROVINCE = "stateprovince";
  public static final String COUNTRY = "country";
  public static final String PRIMARYTELEPHONE = "primarytelephone";
  public static final String OTHERTELEPHONE = "othertelephone";
  public static final String DAY = "day";
  public static final String MONTH = "month";
  public static final String YEAR = "year";
  public static final String FAXNUMBER = "faxnumber";
  public static final String SAVEINADDRESSBOOK = "saveinaddressbook";
  public static final String PASSWORD = "password";
  public static final String PASSWORD_CONFIRMATION = "passwordConfirmation";
  public static final String SUBSCRIBE_NEWSLETTER = "subscribeNewsletter";

  public static List<String> PARAMS = new ArrayList<String>();

  static {
    Collections.addAll(PARAMS, GENDER, FIRSTNAME, LASTNAME, EMAIL, DATEOFBIRTH, DAY, MONTH, YEAR, COMPANYNAME
        , STREETADDRESS, SUBURB, POSTALCODE, CITY, STATEPROVINCE, COUNTRY, PRIMARYTELEPHONE
        , OTHERTELEPHONE, FAXNUMBER, SAVEINADDRESSBOOK, ADDRESS, PASSWORD, PASSWORD_CONFIRMATION
        , SUBSCRIBE_NEWSLETTER);
  }

  /**
   * Create a new address for the customer
   *
   * @return a created address
   */
  public AddressIf createAddressForCustomer(FormMap formMap) {

    AddressIf address = new Address();


    FormField formField = formMap.getField(GENDER);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      address.setGender(formField.getValue());
    }

    formField = formMap.getField(FIRSTNAME);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      address.setFirstName(formField.getValue());
    }

    formField = formMap.getField(LASTNAME);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      address.setLastName(formField.getValue());
    }

    formField = formMap.getField(COMPANYNAME);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      address.setCompany(formField.getValue());
    }

    formField = formMap.getField(STREETADDRESS);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      address.setStreetAddress(formField.getValue());
    }

    formField = formMap.getField(SUBURB);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      address.setSuburb(formField.getValue());
    }

    formField = formMap.getField(POSTALCODE);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      address.setPostcode(formField.getValue());
    }

    formField = formMap.getField(CITY);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      address.setCity(formField.getValue());
    }

    formField = formMap.getField(STATEPROVINCE);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      address.setState(formField.getValue());
    }

    formField = formMap.getField(COUNTRY);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      address.setCountryId(Integer.parseInt(formField.getValue()));
    }

    formField = formMap.getField(PRIMARYTELEPHONE);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      address.setTelephoneNumber(formField.getValue());
    }

    formField = formMap.getField(OTHERTELEPHONE);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      address.setTelephoneNumber1(formField.getValue());
    }

    return address;
  }

  /**
   * Create a customer registration from the onecheckout form
   *
   * @return a created customer registration
   */
  public CustomerRegistrationIf createCustomerRegistration(FormMap formMap) {
    CustomerRegistrationIf customerRegistration = new CustomerRegistration();

    FormField formField = formMap.getField(GENDER);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      customerRegistration.setGender(formField.getValue());
    } else {
      customerRegistration.setGender(DEFAULT_GENDER);
    }

    formField = formMap.getField(FIRSTNAME);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      customerRegistration.setFirstName(formField.getValue());
    }

    formField = formMap.getField(LASTNAME);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      customerRegistration.setLastName(formField.getValue());
    }

    formField = formMap.getField(EMAIL);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      customerRegistration.setEmailAddr(formField.getValue());
    }

    formField = formMap.getField(DATEOFBIRTH);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      customerRegistration.setBirthDate(computeBirthDate(formField.getValue()));
    } else {
      customerRegistration.setBirthDate(new GregorianCalendar());
    }

    formField = formMap.getField(COMPANYNAME);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      customerRegistration.setCompany(formField.getValue());
    }

    formField = formMap.getField(STREETADDRESS);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      customerRegistration.setStreetAddress(formField.getValue());
    }

    formField = formMap.getField(SUBURB);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      customerRegistration.setSuburb(formField.getValue());
    }

    formField = formMap.getField(POSTALCODE);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      customerRegistration.setPostcode(formField.getValue());
    }

    formField = formMap.getField(CITY);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      customerRegistration.setCity(formField.getValue());
    }

    formField = formMap.getField(STATEPROVINCE);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      customerRegistration.setState(formField.getValue());
    }

    formField = formMap.getField(COUNTRY);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      customerRegistration.setCountryId(Integer.parseInt(formField.getValue()));
    }

    formField = formMap.getField(PRIMARYTELEPHONE);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      customerRegistration.setTelephoneNumber(formField.getValue());
    }

    formField = formMap.getField(OTHERTELEPHONE);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      customerRegistration.setTelephoneNumber1(formField.getValue());
    }

    formField = formMap.getField(SUBSCRIBE_NEWSLETTER);
    if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
      customerRegistration.setNewsletter("1");
    } else {
      customerRegistration.setNewsletter("0");
    }

    return customerRegistration;
  }

  /**
   * Convert the date of birth to calendar
   *
   * @param birthDate to convert
   * @return the converted date
   */
  protected Calendar computeBirthDate(String birthDate) {
    String DATE_PATTERN = "yyyy/MM/dd";

    SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
    try {
      Calendar calendar = GregorianCalendar.getInstance();
      calendar.setTime(sdf.parse(birthDate));

      return calendar;
    } catch (ParseException e) {
      log.error("Failed to convert the date. The current date will be set.");
      return GregorianCalendar.getInstance();
    }
  }
}
