/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.example;

import java.util.ArrayList;
import java.util.List;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import com.owlike.genson.Genson;

@Contract(
        name = "chickenchaincode",
        info = @Info(
                title = "Asset Transfer",
                description = "The hyperlegendary asset transfer",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "a.transfer@example.com",
                        name = "Adrian Transfer",
                        url = "https://hyperledger.example.com")))
@Default
public final class AssetTransfer implements ContractInterface {

    private final Genson genson = new Genson();

    private enum AssetTransferErrors {
        ASSET_NOT_FOUND,
        ASSET_ALREADY_EXISTS
    }

    /**
     * Creates some initial assets on the ledger.
     *
     * @param ctx the transaction context
     * @param motherCompany
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void InitLedger(final Context ctx, final String motherCompany) {
        ChaincodeStub stub = ctx.getStub();

        for(int i =0; i < 100; i++){
            Asset transfer = CreateAsset(ctx, "chicken" + i, motherCompany, "", "", "", motherCompany);
            String assetState = genson.serialize(transfer);
            stub.putStringState("chicken" + i, assetState);
        }

    }

    /**
     * Creates a new asset on the ledger.
     *
     * @param ctx the transaction context
     * @param chickenId the ID of the asset being updated
     * @param motherCompany the color of the asset being updated
     * @param chickenFarm the size of the asset being updated
     * @param feedingCompany the owner of the asset being updated
     * @param slaughterHouse the appraisedValue of the asset being updated
     * @param owner
     * @return the created asset
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Asset CreateAsset(final Context ctx, final String chickenId, final String motherCompany, final String chickenFarm,
        final String feedingCompany, final String slaughterHouse, final String owner) {
        ChaincodeStub stub = ctx.getStub();

        if (AssetExists(ctx, chickenId)) {
            String errorMessage = String.format("Asset %s already exists", chickenId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_ALREADY_EXISTS.toString());
        }

        Asset asset = new Asset(chickenId, motherCompany, chickenFarm, feedingCompany, feedingCompany, owner);
        //Use Genson to convert the Asset into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(asset);
        stub.putStringState(chickenId, sortedJson);

        return asset;
    }

    /**
     * Retrieves an asset with the specified ID from the ledger.
     *
     * @param ctx the transaction context
     * @param chickenId the ID of the asset
     * @return the asset found on the ledger if there was one
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Asset ReadAsset(final Context ctx, final String chickenId) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(chickenId);

        if (assetJSON == null || assetJSON.isEmpty()) {
            String errorMessage = String.format("Asset %s does not exist", chickenId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }

        Asset asset = genson.deserialize(assetJSON, Asset.class);
        return asset;
    }

    /**
     * Updates the properties of an asset on the ledger.
     *
     * @param ctx the transaction context
     * @param chickenId the ID of the asset being updated
     * @param motherCompany the color of the asset being updated
     * @param chickenFarm the size of the asset being updated
     * @param feedingCompany the owner of the asset being updated
     * @param slaughterHouse the appraisedValue of the asset being updated
     * @param owner
     * @return the transferred asset
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Asset UpdateAsset(final Context ctx, final String chickenId, final String motherCompany, final String chickenFarm,
        final String feedingCompany, final String slaughterHouse, final String owner) {
        ChaincodeStub stub = ctx.getStub();

        if (!AssetExists(ctx, chickenId)) {
            String errorMessage = String.format("Asset %s does not exist", chickenId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }

        Asset newAsset = new Asset(chickenId, motherCompany, chickenFarm, feedingCompany, slaughterHouse, owner);
        //Use Genson to convert the Asset into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(newAsset);
        stub.putStringState(chickenId, sortedJson);
        return newAsset;
    }

    /**
     * Deletes asset on the ledger.
     *
     * @param ctx the transaction context
     * @param chickenId the ID of the asset being deleted
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteAsset(final Context ctx, final String chickenId) {
        ChaincodeStub stub = ctx.getStub();

        if (!AssetExists(ctx, chickenId)) {
            String errorMessage = String.format("Asset %s does not exist", chickenId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }

        stub.delState(chickenId);
    }

    /**
     * Checks the existence of the asset on the ledger
     *
     * @param ctx the transaction context
     * @param chickenId the ID of the asset
     * @return boolean indicating the existence of the asset
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean AssetExists(final Context ctx, final String chickenId) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(chickenId);

        return (assetJSON != null && !assetJSON.isEmpty());
    }

    /**
     * Changes the owner of a asset on the ledger.
     *
     * @param ctx the transaction context
     * @param chickenId the ID of the asset being transferred
     * @param newOwner the new owner
     * @return the old owner
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String TransferAsset(final Context ctx, final String chickenId, final String newOwner) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(chickenId);

        if (assetJSON == null || assetJSON.isEmpty()) {
            String errorMessage = String.format("Asset %s does not exist", chickenId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, AssetTransferErrors.ASSET_NOT_FOUND.toString());
        }

        Asset asset = genson.deserialize(assetJSON, Asset.class);

        Asset newAsset = new Asset(asset.getChickenId(),asset.getMotherCompany(), asset.getChickenFarm(), asset.getFeedingCompany(), asset.getSlaughterHouse(), newOwner);
        //Use a Genson to conver the Asset into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(newAsset);
        stub.putStringState(chickenId, sortedJson);

        return asset.getOwner();
    }

    /**
     * Retrieves all assets from the ledger.
     *
     * @param ctx the transaction context
     * @return array of assets found on the ledger
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllAssets(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<Asset> queryResults = new ArrayList<Asset>();

        // To retrieve all assets from the ledger use getStateByRange with empty startKey & endKey.
        // Giving empty startKey & endKey is interpreted as all the keys from beginning to end.
        // As another example, if you use startKey = 'asset0', endKey = 'asset9' ,
        // then getStateByRange will retrieve asset with keys between asset0 (inclusive) and asset9 (exclusive) in lexical order.
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result: results) {
            Asset asset = genson.deserialize(result.getStringValue(), Asset.class);
            System.out.println(asset);
            queryResults.add(asset);
        }

        final String response = genson.serialize(queryResults);

        return response;
    }
}

