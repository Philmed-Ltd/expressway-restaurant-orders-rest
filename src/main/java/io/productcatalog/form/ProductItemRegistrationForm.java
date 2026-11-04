package io.productcatalog.form;

import io.productcatalog.validator.UniqueProductItemName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductItemRegistrationForm extends ProductItemEditForm{

    @UniqueProductItemName
    public String getName(){
        return super.getName();
    }
}
