package org.example;

import org.hyperledger.fabric.gateway.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class App {

	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}

	// helper function for getting connected to the gateway
	public static Gateway connect() throws Exception{
		// Load a file system based wallet for managing identities.
		Path walletPath = Paths.get("wallet");
		Wallet wallet = Wallets.newFileSystemWallet(walletPath);
		// load a CCP
		Path networkConfigPath = Paths.get("..", "..", "test-network", "organizations", "peerOrganizations", "org1.example.com", "connection-org1.yaml");

		Gateway.Builder builder = Gateway.createBuilder();
		builder.identity(wallet, "appUser").networkConfig(networkConfigPath).discovery(true);
		return builder.connect();
	}

	public static void main(String[] args) throws Exception {
		// enrolls the admin and registers the user
		try {
			EnrollAdmin.main(null);
			RegisterUser.main(null);
		} catch (Exception e) {
			System.err.println(e);
		}

		// connect to the network and invoke the smart contract
		try (Gateway gateway = connect()) {

			// get the network and contract
			Network network = gateway.getNetwork("mychannel");
			Contract contract = network.getContract("chickenchaincode");
			Scanner input = new Scanner(System.in);
			while (true) {
				byte[] result;
				System.out.println("Enter use case...");
				String app = input.nextLine();
				if (app.equals("motherCompany")) {
					System.out.println("\n");
					System.out.println("Submit Transaction: InitLedger creates the initial set of assets on the ledger.");
					contract.submitTransaction("InitLedger", "Zarbal");
					System.out.println("\n");
					result = contract.evaluateTransaction("GetAllAssets");
					//System.out.println("Evaluate Transaction: GetAllAssets, result: " + new String(result));
					String val = new String(result);
					JSONArray jsonArr = (JSONArray) new JSONParser().parse( val );
					for (Object o : jsonArr) {
						JSONObject jsonObj = (JSONObject) o;
						System.out.println(jsonObj);
					}
				}
				if (app.equals("chickenFarm")) {
					System.out.println("\n");
					System.out.println("Submit Transaction: Chickens been moved to farm.");
					System.out.println("\n");
					result = contract.evaluateTransaction("GetAllAssets");
					String val = new String(result);
					JSONArray jsonArr = (JSONArray) new JSONParser().parse( val );
					for (Object o : jsonArr) {
						JSONObject jsonObj = (JSONObject) o;
						contract.submitTransaction("UpdateAsset", jsonObj.get("chickenId").toString()
								, jsonObj.get("motherCompany").toString()
								, "farm X"
								, jsonObj.get("feedingCompany").toString()
								, jsonObj.get("slaughterHouse").toString()
								, "farm X"
						);
						System.out.println(jsonObj.get("chickenId").toString()+" done.");
					}
				}
				if (app.equals("slaughterHouse")) {
					System.out.println("\n");
					System.out.println("Submit Transaction: Chickens been moved to slaughterHouse.");
					System.out.println("\n");
					result = contract.evaluateTransaction("GetAllAssets");
					String val = new String(result);
					JSONArray jsonArr = (JSONArray) new JSONParser().parse( val );
					for (Object o : jsonArr) {
						JSONObject jsonObj = (JSONObject) o;
						contract.submitTransaction("UpdateAsset", jsonObj.get("chickenId").toString()
								, jsonObj.get("motherCompany").toString()
								, jsonObj.get("chickenFarm").toString()
								, jsonObj.get("feedingCompany").toString()
								, "slaughterhouse X"
								, "slaughterhouse X"
						);
						System.out.println(jsonObj.get("chickenId").toString()+" done.");
					}
				}
				if (app.equals("feedingCompany")) {
					System.out.println("\n");
					System.out.println("Submit Transaction: Chickens feeding.");
					System.out.println("\n");
					result = contract.evaluateTransaction("GetAllAssets");
					String val = new String(result);
					JSONArray jsonArr = (JSONArray) new JSONParser().parse( val );
					for (Object o : jsonArr) {
						JSONObject jsonObj = (JSONObject) o;
						contract.submitTransaction("UpdateAsset", jsonObj.get("chickenId").toString()
								, jsonObj.get("motherCompany").toString()
								, jsonObj.get("chickenFarm").toString()
								, "feeding company X"
								, jsonObj.get("slaughterHouse").toString()
								, jsonObj.get("owner").toString()
						);
						System.out.println(jsonObj.get("chickenId").toString()+" done.");
					}
				}
				if (app.equals("getAll")) {
					System.out.println("\n");
					System.out.println("Get all chickens.");
					System.out.println("\n");
					result = contract.evaluateTransaction("GetAllAssets");
					String val = new String(result);
					JSONArray jsonArr = (JSONArray) new JSONParser().parse( val );
					for (Object o : jsonArr) {
						JSONObject jsonObj = (JSONObject) o;
						System.out.println(jsonObj);
					}
				}
				if (app.equals("exit")) {
					break;
				}
			}

		}
		catch(Exception e){
			System.err.println(e);
		}

	}
}
