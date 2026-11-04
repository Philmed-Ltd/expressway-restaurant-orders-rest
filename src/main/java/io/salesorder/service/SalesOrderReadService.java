package io.salesorder.service;

import io.lib.service.BaseJpaRepoReadService;
import io.micrometer.common.util.StringUtils;
import io.salesorder.entity.SalesOrder;
import io.salesorder.form.SalesOrderFetchForm;
import io.salesorder.repository.SalesOrderRepository;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SalesOrderReadService extends BaseJpaRepoReadService<SalesOrder, SalesOrderRepository> {

    public Page<SalesOrder> listSalesOrders(SalesOrderFetchForm form) {
        Specification<SalesOrder> spec = repository.notDeleted();

        if (StringUtils.isNotBlank(form.getQuery())) {
            spec = spec.and(customerNameLike(form.getQuery()));
        }

        if (form.getCustomerPhoneNumber() != null){
            spec = spec.and(customerPhoneNumberLike(form.getCustomerPhoneNumber()));
        }

        if (form.getCustomerEmail() != null){
            spec = spec.and(customerEmailLike(form.getCustomerEmail()));
        }

        spec = spec.and(fetchCustomer());

        return repository.findAll(spec, repository.defaultPageable(form));
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

    private Specification<SalesOrder> fetchCustomer() {
        return (root, q, cb) -> {
            if (q.getResultType() != Long.class && q.getResultType() != long.class) {
                root.fetch("customer", JoinType.LEFT);
            }
            return cb.conjunction();
        };
    }
}
