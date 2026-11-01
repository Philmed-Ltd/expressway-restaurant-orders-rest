package io.productcatalog.form;

import io.lib.form.SessionUserIdForm;
import io.productcatalog.entity.ProductStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductStatusEditingForm extends SessionUserIdForm {
    private ProductStatus status;
}
