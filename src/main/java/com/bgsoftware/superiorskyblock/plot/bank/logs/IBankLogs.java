package com.bgsoftware.superiorskyblock.plot.bank.logs;

import com.bgsoftware.superiorskyblock.api.plot.bank.BankTransaction;

import java.util.List;
import java.util.UUID;

public interface IBankLogs {

    int getLastTransactionPosition();

    List<BankTransaction> getTransactions();

    List<BankTransaction> getTransactions(UUID playerUUID);

    void addTransaction(BankTransaction bankTransaction, UUID senderUUID, boolean loadFromDatabase);

}
