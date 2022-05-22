
'use strict';

var express = require('express');
var bodyParser = require('body-parser');
var app = express();
app.use(bodyParser.json());

const { Gateway, Wallets } = require('fabric-network');
const fs = require('fs');
const path = require('path');

const chaincodeName = 'chickenchaincode';

app.listen(5050, function(){
    console.log("info",'Server is running at port : ' + 5050);
});


app.post('/api/initledger', async function (req, res){
   initLedger(req, res);
});

app.put('/api/addchicken', async function (req, res){
    addChicken(req, res);
});

app.put('/api/updatechicken', async function (req, res){
    updateChicken(req, res);
});

app.get('/api/allchickens', async function (req, res){
    getAllChickens(req, res);
});

//---------------------------------------------------------------------------------------------------------

async function initLedger(req, res) {
    try {        
        const ccpPath = path.resolve(__dirname, '..', '..', 'test-network', 'organizations', 'peerOrganizations', 'org1.example.com', 'connection-org1.json');
        let ccp = JSON.parse(fs.readFileSync(ccpPath, 'utf8'));
        const walletPath = path.join(process.cwd(), 'wallet');
        const wallet = await Wallets.newFileSystemWallet(walletPath);
        console.log(`Wallet path: ${walletPath}`);
        const identity = await wallet.get('appUser');
        if (!identity) {
            console.log('An identity for the user "appUser" does not exist in the wallet');
            console.log('Run the registerUser.js application before retrying');
            return;
        }
        const gateway = new Gateway();
        await gateway.connect(ccp, { wallet, identity: 'appUser', discovery: { enabled: true, asLocalhost: true } });
        const network = await gateway.getNetwork('mychannel');
        const contract = network.getContract(chaincodeName);


        console.log(req.params.chickensCount);
        await contract.submitTransaction('InitLedger', req.body.chickensCount);
        console.log('Transaction has been submitted');
        //res.send('Transaction has been submitted');
        res.status(200).json({message : 'Transaction has been submitted'});

        await gateway.disconnect();

    } catch (error) {
        console.error(`Failed to submit transaction: ${error}`);
        res.status(500).json({error: error});
        process.exit(1);
    }
}


async function addChicken(req, res) {
    try {
        const ccpPath = path.resolve(__dirname, '..', '..', 'test-network', 'organizations', 'peerOrganizations', 'org1.example.com', 'connection-org1.json');
        let ccp = JSON.parse(fs.readFileSync(ccpPath, 'utf8'));
        const walletPath = path.join(process.cwd(), 'wallet');
        const wallet = await Wallets.newFileSystemWallet(walletPath);
        console.log(`Wallet path: ${walletPath}`);
        const identity = await wallet.get('appUser');
        if (!identity) {
            console.log('An identity for the user "appUser" does not exist in the wallet');
            console.log('Run the registerUser.js application before retrying');
            return;
        }
        const gateway = new Gateway();
        await gateway.connect(ccp, { wallet, identity: 'appUser', discovery: { enabled: true, asLocalhost: true } });
        const network = await gateway.getNetwork('mychannel');
        const contract = network.getContract(chaincodeName);

        await contract.submitTransaction('CreateAsset', req.body.chickenId, req.body.motherCompany, req.body.chickenFarm, req.body.feedingCompany, req.body.slaughterHouse, req.body.owner);
        console.log(`Transaction has been submitted`);
        console.log(req.body.chickenId);
        //res.send('Transaction has been submitted');
        res.status(200).json({message : 'Transaction has been submitted'});

        await gateway.disconnect();

    } catch (error) {
        console.error(`Failed to submit transaction: ${error}`);
        res.status(500).json({error: error});
        process.exit(1);
    }
}

async function updateChicken(req, res) {
    try {
        const ccpPath = path.resolve(__dirname, '..', '..', 'test-network', 'organizations', 'peerOrganizations', 'org1.example.com', 'connection-org1.json');
        let ccp = JSON.parse(fs.readFileSync(ccpPath, 'utf8'));
        const walletPath = path.join(process.cwd(), 'wallet');
        const wallet = await Wallets.newFileSystemWallet(walletPath);
        console.log(`Wallet path: ${walletPath}`);
        const identity = await wallet.get('appUser');
        if (!identity) {
            console.log('An identity for the user "appUser" does not exist in the wallet');
            console.log('Run the registerUser.js application before retrying');
            return;
        }
        const gateway = new Gateway();
        await gateway.connect(ccp, { wallet, identity: 'appUser', discovery: { enabled: true, asLocalhost: true } });
        const network = await gateway.getNetwork('mychannel');
        const contract = network.getContract(chaincodeName);

        await contract.submitTransaction('UpdateAsset', req.body.chickenId, req.body.motherCompany, req.body.chickenFarm, req.body.feedingCompany, req.body.slaughterHouse, req.body.owner);
        console.log(`Transaction has been submitted`);
        //res.send('Transaction has been submitted');
        res.status(200).json({message : 'Transaction has been submitted'});

        await gateway.disconnect();

    } catch (error) {
        console.error(`Failed to submit transaction: ${error}`);
        res.status(500).json({error: error});
        process.exit(1);
    }
}

async function getAllChickens(req, res) {
    try {
        const ccpPath = path.resolve(__dirname, '..', '..', 'test-network', 'organizations', 'peerOrganizations', 'org1.example.com', 'connection-org1.json');
        let ccp = JSON.parse(fs.readFileSync(ccpPath, 'utf8'));
        const walletPath = path.join(process.cwd(), 'wallet');
        const wallet = await Wallets.newFileSystemWallet(walletPath);
        console.log(`Wallet path: ${walletPath}`);
        const identity = await wallet.get('appUser');
        if (!identity) {
            console.log('An identity for the user "appUser" does not exist in the wallet');
            console.log('Run the registerUser.js application before retrying');
            return;
        }
        const gateway = new Gateway();
        await gateway.connect(ccp, { wallet, identity: 'appUser', discovery: { enabled: true, asLocalhost: true } });
        const network = await gateway.getNetwork('mychannel');
        const contract = network.getContract(chaincodeName);

        const result = await contract.evaluateTransaction('GetAllAssets');
        console.log(JSON.parse(result)[0]["Record"])
        console.log(`Transaction has been evaluated, result is: ${result.toString()}`);
        res.status(200).json(JSON.parse(result.toString()));

        await gateway.disconnect();

    } catch (error) {
        console.error(`Failed to submit transaction: ${error}`);
        res.status(500).json({error: error});
        process.exit(1);
    }
}


