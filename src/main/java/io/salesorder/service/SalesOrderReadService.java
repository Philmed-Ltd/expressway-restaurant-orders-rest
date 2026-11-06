package io.salesorder.service;

import io.lib.form.BaseFetchForm;
import io.lib.service.BaseJpaRepoReadService;
import io.micrometer.common.util.StringUtils;
import io.salesorder.entity.SalesOrder;
import io.salesorder.repository.SalesOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class SalesOrderReadService extends BaseJpaRepoReadService<SalesOrder, SalesOrderRepository> {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[\\d\\s\\-()]{5,}$");

    public Page<SalesOrder> listSalesOrders(BaseFetchForm form) {
        Specification<SalesOrder> spec = repository.notDeleted();

        if (StringUtils.isNotBlank(form.getQuery())) {
            spec = spec.and(matchesCustomer(form.getQuery().trim()));
        }

        return repository.findAll(spec, repository.defaultPageable(form));
    }

    public Page<SalesOrder> listSalesOrdersForCustomer(String customerEntityId, BaseFetchForm form) {
        Specification<SalesOrder> spec = repository.notDeleted()
                .and(repository.customerIdIs(customerEntityId));
        return repository.findAll(spec, repository.defaultPageable(form));
    }

    private Specification<SalesOrder> matchesCustomer(String query) {
        if (query.contains("@")) {
            return customerEmailLike(query);
        }
        if (PHONE_PATTERN.matcher(query).matches()) {
            return customerPhoneNumberLike(query);
        }
        return customerNameLike(query);
    }

    private Specification<SalesOrder> customerNameLike(String query) {
        String like = "%" + query.toLowerCase() + "%";
        return (root, q, cb) -> cb.like(cb.lower(root.get("customer").get("name")), like);
    }

    private Specification<SalesOrder> customerPhoneNumberLike(String query) {
        String like = "%" + query.toLowerCase() + "%";
        return (root, q, cb) -> cb.like(cb.lower(root.get("customer").get("phoneNumber")), like);
    }

    private Specification<SalesOrder> customerEmailLike(String query) {
        String like = "%" + query.toLowerCase() + "%";
        return (root, q, cb) -> cb.like(cb.lower(root.get("customer").get("email")), like);
    }
}
