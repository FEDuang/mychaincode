package org.example.modles;

import lombok.Builder;
import lombok.Data;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType
@Data
@Builder
public class Bill {
    private final String docType = "bill";
    @Property
    private String billID;
    @Property
    private String shipperID;
    @Property
    private String carrierID;
    @Property
    private String consigneeID;

    @Property
    private float KGS;
    @Property
    private int volume;
    @Property
    private double price;
    @Property
    private Long creationDate;
    @Property Long closingDate;
    @Property
    private boolean isPrepaid;

    @Property
    private boolean auditStatus;
    @Property
    private String ownerID;
    @Property
    private int status;
}
