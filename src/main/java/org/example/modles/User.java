package org.example.modles;

import lombok.Builder;
import lombok.Data;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.hyperledger.fabric.shim.ChaincodeStub;

@Data
@DataType
@Builder
public class User {
    @Property
    private String userID;
    @Property
    private String role;
    @Property
    private String Capacity;
    @Property
    private double Cash;
    @Property
    private String Hash;

}
