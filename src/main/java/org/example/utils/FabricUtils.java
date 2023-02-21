package org.example.utils;

import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.util.Arrays;

public class FabricUtils {

    public static String getQueryResultForQueryString(ChaincodeStub stub, String queryString) {
        System.out.printf("- getQueryResultForQueryString queryString:\n%s\n", queryString);
        QueryResultsIterator<KeyValue> resultIterator = stub.getQueryResult(queryString);
        String buffer = constructQueryResponseFromIterator(resultIterator);
        System.out.printf("- getQueryResultForQueryString queryResult:\n%s\n", buffer);
        return buffer;
    }

    public static String constructQueryResponseFromIterator(QueryResultsIterator<KeyValue> resultsIterator) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        boolean bArrayMemberAlreadyWritten = false;
        for (KeyValue queryResponse : resultsIterator) {
            if (bArrayMemberAlreadyWritten) {
                builder.append(",");
            }
            builder.append("{\"Key\":");
            builder.append("\"");
            builder.append(queryResponse.getKey());
            builder.append("\"");

            builder.append(", \"Record\":");
            // Record is a JSON object, so we write as-is
            builder.append(Arrays.toString(queryResponse.getValue()));
            builder.append("}");
            bArrayMemberAlreadyWritten = true;
        }
        builder.append("]");

        return builder.toString();
    }
}
