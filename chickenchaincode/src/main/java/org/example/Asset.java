/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.example;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Asset {

    @Property()
    private final String chickenId;

    @Property()
    private final String motherCompany;

    @Property()
    private final String chickenFarm;

    @Property()
    private final String feedingCompany;

    @Property()
    private final String slaughterHouse;

    @Property
    private final String owner;


    public String getChickenId() {
        return chickenId;
    }

    public String getMotherCompany() {
        return motherCompany;
    }

    public String getChickenFarm() {
        return chickenFarm;
    }

    public String getFeedingCompany() {
        return feedingCompany;
    }

    public String getSlaughterHouse() {
        return slaughterHouse;
    }

    public String getOwner() {
        return owner;
    }

    public Asset(@JsonProperty("chickenId") final String chickenId, @JsonProperty("motherCompany") final String motherCompany,
            @JsonProperty("chickenFarm") final String chickenFarm, @JsonProperty("feedingCompany") final String feedingCompany,
            @JsonProperty("slaughterHouse") final String slaughterHouse, @JsonProperty("owner") final String owner) {
        this.chickenId = chickenId;
        this.motherCompany = motherCompany;
        this.chickenFarm = chickenFarm;
        this.feedingCompany = feedingCompany;
        this.slaughterHouse = slaughterHouse;
        this.owner = owner;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Asset other = (Asset) obj;

        return Objects.deepEquals(
                new String[] {getChickenId(), getMotherCompany(), getChickenFarm(), getFeedingCompany(), getSlaughterHouse(), getOwner()},
                new String[] {other.getChickenId(), other.getMotherCompany(), other.getChickenFarm(), other.getFeedingCompany(), other.getSlaughterHouse(), other.getOwner()});
                
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChickenId(), getMotherCompany(), getChickenFarm(), getFeedingCompany(), getSlaughterHouse(), getOwner());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [chickenId=" + chickenId + ", motherCompany="
                + motherCompany + ", chickenFarm=" + chickenFarm + ", feedingCompany=" + feedingCompany + ", slaughterHouse=" + slaughterHouse + ", owner=" + owner + "]";
    }
}

