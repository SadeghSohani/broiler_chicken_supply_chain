
'use strict';

// Deterministic JSON.stringify()
const stringify  = require('json-stringify-deterministic');
const sortKeysRecursive  = require('sort-keys-recursive');
const { Contract } = require('fabric-contract-api');

class AssetTransfer extends Contract {

    async InitLedger(ctx) {
        const assets = [
            {
                chickenId: 'chicken0',
                motherCompany: 'Zarbal',
                chickenFarm: '',
                feedingCompany: '',
                slaughterHouse: '',
                owner: 'Zarbal'
            },
            {
                chickenId: 'chicken1',
                motherCompany: 'Zarbal',
                chickenFarm: '',
                feedingCompany: '',
                slaughterHouse: '',
                owner: 'Zarbal'
            },
            {
                chickenId: 'chicken2',
                motherCompany: 'Zarbal',
                chickenFarm: '',
                feedingCompany: '',
                slaughterHouse: '',
                owner: 'Zarbal'
            },
            {
                chickenId: 'chicken3',
                motherCompany: 'Zarbal',
                chickenFarm: '',
                feedingCompany: '',
                slaughterHouse: '',
                owner: 'Zarbal'
            },
            {
                chickenId: 'chicken4',
                motherCompany: 'Zarbal',
                chickenFarm: '',
                feedingCompany: '',
                slaughterHouse: '',
                owner: 'Zarbal'
            },
            {
                chickenId: 'chicken5',
                motherCompany: 'Zarbal',
                chickenFarm: '',
                feedingCompany: '',
                slaughterHouse: '',
                owner: 'Zarbal'
            },
            {
                chickenId: 'chicken6',
                motherCompany: 'Zarbal',
                chickenFarm: '',
                feedingCompany: '',
                slaughterHouse: '',
                owner: 'Zarbal'
            },
            {
                chickenId: 'chicken7',
                motherCompany: 'Zarbal',
                chickenFarm: '',
                feedingCompany: '',
                slaughterHouse: '',
                owner: 'Zarbal'
            },
            {
                chickenId: 'chicken8',
                motherCompany: 'Zarbal',
                chickenFarm: '',
                feedingCompany: '',
                slaughterHouse: '',
                owner: 'Zarbal'
            },
            {
                chickenId: 'chicken9',
                motherCompany: 'Zarbal',
                chickenFarm: '',
                feedingCompany: '',
                slaughterHouse: '',
                owner: 'Zarbal'
            },
        ];

        for (const asset of assets) {
            //asset.docType = 'asset';
            await ctx.stub.putState(asset.chickenId, Buffer.from(stringify(sortKeysRecursive(asset))));
        }
    }

    async CreateAsset(ctx, chickenId, motherCompany, chickenFarm, feedingCompany, slaughterHouse, owner) {
        const exists = await this.AssetExists(ctx, chickenId);
        if (exists) {
            throw new Error(`The asset ${chickenId} already exists`);
        }

        const asset = {
            chickenId: chickenId,
            motherCompany: motherCompany,
            chickenFarm: chickenFarm,
            feedingCompany: feedingCompany,
            slaughterHouse: slaughterHouse,
            owner: owner,
        };
        await ctx.stub.putState(chickenId, Buffer.from(stringify(sortKeysRecursive(asset))));
        return JSON.stringify(asset);
    }

    async ReadAsset(ctx, chickenId) {
        const assetJSON = await ctx.stub.getState(chickenId); 
        if (!assetJSON || assetJSON.length === 0) {
            throw new Error(`The asset ${chickenId} does not exist`);
        }
        return assetJSON.toString();
    }

    async UpdateAsset(ctx, chickenId, motherCompany, chickenFarm, feedingCompany, slaughterHouse, owner) {
        const exists = await this.AssetExists(ctx, chickenId);
        if (!exists) {
            throw new Error(`The asset ${chickenId} does not exist`);
        }

        const updatedAsset = {
            chickenId: chickenId,
            motherCompany: motherCompany,
            chickenFarm: chickenFarm,
            feedingCompany: feedingCompany,
            slaughterHouse: slaughterHouse,
            owner: owner,
        };
        return ctx.stub.putState(chickenId, Buffer.from(stringify(sortKeysRecursive(updatedAsset))));
    }

    async DeleteAsset(ctx, chickenId) {
        const exists = await this.AssetExists(ctx, chickenId);
        if (!exists) {
            throw new Error(`The asset ${chickenId} does not exist`);
        }
        return ctx.stub.deleteState(chickenId);
    }

    async AssetExists(ctx, chickenId) {
        const assetJSON = await ctx.stub.getState(chickenId);
        return assetJSON && assetJSON.length > 0;
    }

    async GetAllAssets(ctx) {
        const allResults = [];
        const iterator = await ctx.stub.getStateByRange('', '');
        let result = await iterator.next();
        while (!result.done) {
            const strValue = Buffer.from(result.value.value.toString()).toString('utf8');
            let record;
            try {
                record = JSON.parse(strValue);
            } catch (err) {
                console.log(err);
                record = strValue;
            }
            allResults.push(record);
            result = await iterator.next();
        }
        return JSON.stringify(allResults);
    }
}

module.exports = AssetTransfer;

