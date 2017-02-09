package com.pos.cashier.referensi;

import java.text.DecimalFormat;

public class Constant {
    // CONFIG
    public static final String KEY_BASE_URL          = "base_url";
    public static final String KEY_USER_LOCATION_ID  = "user_location_id";

    // DATABASE
    public static final String SALESMAN_NAME    = "tbl_m_salesman";
    public static final String STOCK_NAME       = "tbl_m_stock";
    public static final String TRANSACTION_NAME = "tbl_t_transaction";

    // SALESMAN FIELD
    public static final String KEY_EMPLOYEE_ID    = "employee_id";
    public static final String KEY_EMPLOYEE_NAME  = "employee_name";
    public static final String KEY_LOCATION_CODE  = "location_code";
    public static final String KEY_USER_NAME      = "user_name";
    public static final String KEY_PASSWORD_VALUE = "password_value";

    // STOCK FIELD
    public static final String KEY_ITEM_ID     = "item_id";
    public static final String KEY_BARCODE     = "barcode";
    public static final String KEY_ITEM_NAME   = "item_name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_ITEM_PRICE  = "item_price";
    public static final String KEY_QTY         = "qty";

    // TRANSACTION FIELD
    public static final String KEY_INV_ID      = "inv_id";
    public static final String KEY_STOCK_ID    = "stock_id";
    public static final String KEY_SALESMAN_ID = "salesman_id";
    public static final String KEY_LOCATION_ID = "location_id";
    public static final String KEY_AMOUNT      = "amount";
    public static final String KEY_DATE        = "date";
    public static final String KEY_STATUS      = "status";

    // INTENT KEY
    public static final String KEY_INTENT_TRANSACTION = "transaction";
    public static final String KEY_INTENT_STATUS      = "status";

    public static String currencyFormater(Double value) {
        DecimalFormat myFormatter = new DecimalFormat("###,###,###");
        return myFormatter.format(value);
    }
}
