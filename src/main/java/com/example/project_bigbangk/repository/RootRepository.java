// Created by RayS
// Creation date 3-12-2021

/* Ray: RootRepository alvast gemaakt, bleek een methode in mijn voorbeelden te zitten waarin die werd aangeroepen.
 * Dus voor Address-Service maar gelijk meegenomen
 */

package com.example.project_bigbangk.repository;

import com.example.project_bigbangk.model.*;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RootRepository {

    private IClientDAO clientDAO;
    private IAddressDAO addressDAO;
    private IWalletDAO walletDAO;
    private IAssetDAO assetDAO;
    private final IPriceHistoryDAO priceHistoryDAO;
    private final int AMOUNT_OF_ASSETS = 20;

    public RootRepository(IClientDAO clientDAO, IAddressDAO addressDAO, IWalletDAO walletDAO, IPriceHistoryDAO priceHistoryDAO, IAssetDAO assetDAO) {
        this.clientDAO = clientDAO;
        this.addressDAO = addressDAO;
        this.walletDAO = walletDAO;
        this.priceHistoryDAO = priceHistoryDAO;
        this.assetDAO = assetDAO;
    }

    // CLIENT

    public Client findClientByEmail(String email) {
        Client client = clientDAO.findClientByEmail(email);
        if (client != null) {
            Address adress = findAddressByEmail(email);
            client.setAddress(adress);
            //ToDO findWalletByEmail
        }
        return client;
    }


    public Address findAddressByEmail(String email) {
        Address address = addressDAO.findAddressByEmail(email);
        return address;
    }

    /**
     * Saves address, wallet and client seperately in Database.
     *
     * @param client
     */
    public void createNewlyRegisteredClient(Client client) {
        addressDAO.saveAddress(client.getAddress());
        walletDAO.saveNewWallet(client.getWallet());
        clientDAO.saveClient(client);
    }

    //PriceHistory
    public void savePriceHistories(List<PriceHistory> priceHistories) {
        boolean saveAssets = assetDAO.getNumberOfAssets() != AMOUNT_OF_ASSETS;
        for (PriceHistory priceHistory : priceHistories) {
            if (saveAssets) {
                assetDAO.saveAsset(priceHistory.getAsset());
            }
            priceHistoryDAO.savePriceHistory(priceHistory);
        }
    }

    //Asset
    public List<Asset> getAllAssets() {
        List<Asset> assets = assetDAO.getAllAssets();
        if (assets != null) {
            for (Asset asset : assets) {
                asset.setCurrentPrice(priceHistoryDAO.getCurrentPriceByAssetCodeName(asset.getAssetCodeName()));
            }
        }
        return assets;
    }

    // WALLET

    public void saveNewWallet(Wallet wallet) {
        walletDAO.saveNewWallet(wallet);
    }
    public Wallet findWalletByIban(String iban) {
        return walletDAO.findWalletByIban(iban);
    }

    public void updateWalletBalanceAndAsset(Wallet wallet, Asset asset) {
        walletDAO.updateBalance(wallet);
        walletDAO.updateWalletAssets(wallet, asset);
    }

    public Wallet findWalletWithAssetByIban(String iban) {
      Wallet wallet = walletDAO.findWalletByIban(iban);
      if (wallet == null) {
         return wallet;
      }
      Map<Asset, Double> assetWithAmountMap = new HashMap<>();
      for (AssetCode_Name assetCode_name: AssetCode_Name.values()) {
          assetWithAmountMap.put(assetDAO.findAssetByCode(assetCode_name.getAssetCode()), walletDAO.findAmountOfAsset(iban, assetCode_name.getAssetCode()));
      }
      wallet.setAsset(assetWithAmountMap);
      return wallet;
   }
}
