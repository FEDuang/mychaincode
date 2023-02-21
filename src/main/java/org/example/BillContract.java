package org.example;

import com.google.gson.Gson;
import org.example.modles.Bill;
import org.example.modles.User;
import org.example.utils.FabricUtils;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

@Contact
@Default
public class BillContract implements ContractInterface {
    Gson gson = new Gson();

    @Override
    public Context createContext(ChaincodeStub stub) {
        return new BillContext(stub);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Bill applyBill(final BillContext ctx, final String billID, final String carrierID, final String consigneeID,
                          final float KGS, final int volume, final double price, final Long creationDate, final Long closingDate, final double freight, final int isPrepaid) {
        ChaincodeStub stub = ctx.getStub();
        ctx.freight = freight;
        String applicantID = ctx.getClientIdentity().getId();

        String billState = stub.getStringState(billID);
        if (!billState.isEmpty()) {
            String errorMessage = String.format("Bill %s already exists", billID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage);
        }

        if (isPrepaid == 1) {
            String shipperState = stub.getStringState(applicantID);
            User shipper = gson.fromJson(shipperState, User.class);
            if ((shipper.getCash() - freight) < 0) {
                String errorMessage = String.format("shipper %s deposit is insufficient to cover freight", applicantID);
                System.out.println(errorMessage);
                throw new ChaincodeException(errorMessage);
            }
        }

        Bill bill = Bill.builder()
                .billID(billID).shipperID(applicantID).carrierID(carrierID).consigneeID(consigneeID)
                .KGS(KGS).volume(volume).price(price).creationDate(creationDate).closingDate(closingDate).isPrepaid(isPrepaid == 1)
                .status(0)
                .build();
        billState = gson.toJson(bill);
        stub.putStringState(billID, billState);
        return bill;
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Bill getBill(final Context ctx, final String billID) {
        return getBillInstance(ctx.getStub(), billID);
    }

    @Transaction
    public String queryBillWithKeyValue(final Context ctx, String key, String value) {
        ChaincodeStub stub = ctx.getStub();

        String queryString = String.format("{\"selector\":{\"docType\":\"bill\",\"%s\":\"%s\"}}", key, value);

        return FabricUtils.getQueryResultForQueryString(stub, queryString);
    }

    @Transaction
    public String queryBillWithQueryString(final Context ctx, String queryString) {
        ChaincodeStub stub = ctx.getStub();

        return FabricUtils.getQueryResultForQueryString(stub, queryString);
    }

    @Transaction
    public Bill confirmAudit(final Context ctx, String billID) {
        ChaincodeStub stub = ctx.getStub();
        String applicantRole = ctx.getClientIdentity().getAttributeValue("role");

        if (applicantRole.equals("customs")) {
            String errorMessage = "Insufficient user rights";
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage);
        }

        Bill bill = getBillInstance(stub, billID);
        bill.setAuditStatus(true);
        stub.putStringState(bill.getBillID(), gson.toJson(bill));
        return bill;
    }

    private Bill getBillInstance(final ChaincodeStub stub, String billID) {
        String billState = stub.getStringState(billID);
        if (billState.isEmpty()) {
            String errorMessage = String.format("BL %s does not exits", billID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "BILL_NOT_FOUNT");
        }
        return gson.fromJson(billState, Bill.class);
    }

    private User getUserInstance(final ChaincodeStub stub, String userID) {
        String userState = stub.getStringState(userID);
        if (userState.isEmpty()) {
            String errorMessage = String.format("User %s does not exits", userID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "User_NOT_FOUNT");
        }
        return gson.fromJson(userState, User.class);
    }

    public String test(ChaincodeStub stub, String arg1){
        return arg1 + "this is a test";
    }

}
