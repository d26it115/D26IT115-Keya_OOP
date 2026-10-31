package service;

import model.Account;

public interface InterestBearing {

    default double yearlyInterest() {
        return ((Account) this).getBalance()
                * ((Account) this).interestRate()
                / 100.0;
    }
}