package io.salesorder.form;

import io.lib.form.BaseFetchForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalesOrderFetchForm extends BaseFetchForm {

    private String customerPhoneNumber;

    private String customerEmail;

}
