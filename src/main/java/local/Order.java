package local;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Order_table")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long productId;
    private Integer qty;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;

    @PostPersist
    public void onPostPersist() {
        Ordered ordered = new Ordered();
        BeanUtils.copyProperties(this, ordered);
        ordered.publishAfterCommit();
    }

    @PostUpdate
    public  void onPostUpdate(){
        System.out.println("Order Info Updated,,,");
    }

    @PreRemove
    public void onPreRemove() {
        OrderCanceled orderCanceled = new OrderCanceled();
        BeanUtils.copyProperties(this, orderCanceled);
        orderCanceled.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        local.external.Cancellation cancellation = new local.external.Cancellation();
        // mappings goes here
        cancellation.setOrderId(this.getId());
        cancellation.setStatus("DeliveryCancelled..");

        OrderApplication.applicationContext.getBean(local.external.CancellationService.class)
                .cancel(cancellation);


    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

}
