# Filecoin Blockchain with Java and Spring Boot

This repository showcase implementation of generating SECP256K1 address, BLS Filecoin addresses offline,
also this repository showcase implementation of signing ECDSA, BLS transaction offline on Filecoin.

The source code can be found in src directory.

##### Please note this is a local/algorithmic implementation of addresses and signing.

## File Coin Lotus Installation Steps (For macOS):

1. Check if you already have the XCode Command Line Tools installed via the CLI, run:
   ```shell
   xcode-select -p
   ``` 
2. If this command returns a path, then you have Xcode already installed
   ```shell 
   /Library/Developer/CommandLineTools
    ```
3. If the above command doesn’t return a path, install Xcode:
   ```shell
   xcode-select --install
    ```
4. Use the command brew install to install the following packages:
   ```shell
    brew install go bzr jq pkg-config rustup hwloc
    ```
5. Clone the repository:
   ```shell
    git clone https://github.com/filecoin-project/lotus.git
    ```
6. Navigate to cloned repository:
   ```shell
    cd lotus/
    ```
7. Checkout repository to the latest release
   ```shell
   git checkout v1.13.2
   ``` 
   Latest release can be found here: https://github.com/filecoin-project/lotus/releases <br/><br/>
8. Build and install Lotus:<br/>
   For Main network
    ```shell
       make clean && make all
    ```
   For Test network
    ```shell
       make clean calibnet
    ```
   And then run
   ```shell
       sudo make install
    ```
9. To start Lotus Daemon in Lite node
   ```shell
   FULLNODE_API_INFO=wss://api.chain.love lotus daemon --lite
   ```
10. To start Lotus Daemon in full node
    ```shell
    lotus daemon
      ```

## Obtain token to call FileCoin lotus APIs

1. Execute: ``lotus auth create-token --perm <read,write,sign,admin>`` e.g.: For obtaining admin
   token ``lotus auth create-token --perm admin``
2. A token will be generated, use that token to call all subsequent APIs by passing in Authorization Header as Bearer
   Token.

## Obtain test FIL

https://faucet.calibration.fildev.network/

## Tools for Calibration Testnet

https://calibration.filscan.io/

## Using both lotus node and calibration rpc

Lotus node either installed as full node or lite node does not sync the FILs and wallets, so this project is using
calibration glif node RPC URL ``https://calibration.node.glif.io/rpc/v0`` for few of the RPC API methods like get
balance and push
transaction/message to the pool.

Please note that the calibration glif RPC URL supports only few of the methods which can be found at
https://calibration.node.glif.io/. That is why local RPC API URL has been used with it.

## Important Lotus Commands

### To get the time stamp of the recently mined block on local lotus filecoin

```shell
date -r $(lotus chain getblock $(lotus chain head) | jq .Timestamp)
```

