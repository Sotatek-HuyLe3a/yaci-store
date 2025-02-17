### **Table of content:**
### 1. [Goals](#goals)
### 2. [Yaci Store - High Level Design](#highlevel-design)
### 3. [Store - Components](#store-design)
### 4. [Handling Rollbacks](#handling-rollbacks)
### 5. [Idempotency](#idempotency)

<a id="goals"></a>
## Goals
Some of the high-level goals of Yaci Store are:

- Providing a Java library for creating scoped indexers. 
- Handling the common logic required for data indexing. 
- Allowing applications to select the data they wish to index. 
- Enabling developers to filter data based on various conditions. 
- Permitting developers to override default behaviors. 
- Managing rollbacks. 
- Offering an out-of-the-box application for indexing common data.

<a id="highlevel-design"></a>
## Yaci Store - Design

![High Level Design](images/component-diagram.png)

### Core

The core module is the backbone of Yaci Store. It is responsible for reading data from Cardano blockchain and broadcasting events.
It also monitors and records the current point in the database.
Events are published as ``Spring events``, so developers can write their own Spring event listeners to listen these events and process them accordingly.

### Stores

Each store is a specialized module designed for a specific data type or use case.

A store has the ability to:

- Listen to events published by the core module.
- Process data.
- Store data in a persistent store.
- Publish derived events. (Optional)
- Provide REST endpoints to retrieve data (optional).

Each of the above functionalities can be overridden by developers through the use of Spring Beans.

**Note:** To simplify the rollback process, stores do not handle any data aggregation.

### Aggregates

Aggregates are modules that handle different kind of data aggregation. They are responsible for aggregating data from different stores and persisting them in a persistent store.
Currently, the only available aggregate is "Account", which provides account balance related data. It depends on the "utxo" store and the event published by utxo store.
But status of this module is still **experimental**.

### Persistence

By default, Yaci Store uses a relational database to store data. The database schema is generated automatically by Flyway.
Supported databases include: **PostgreSQL**, **MySQL**, **H2**

**An application can also use a custom persistence store by implementing a store's Storage api.**

### Tx Submission

The "submit" module provides the ability to submit transactions to nodes directly, either through n2c or the submit API.

<a id="store-design"></a>
## Stores - Components
<hr>

![Store Design](images/store-design.png)

A **store** is composed of the following components:

**1. Processor:** A processor is responsible for processing data. It listens to events published by the core module and processes the data contained in the event. A processor can also publish derived events.
For example: ``UtxoProcessor`` in the ``utxo`` store listens to TransactionEvent and processes the UTxOs contained in the transaction. It also publishes ``AddressUtxo`` derived event for each UTxO.

Developer can write their own processor by listening to events published by the core module.

**2. Storage:** A storage defines the interface for storing data. The implementation of a storage api is responsible for storing data in a persistent store.
By default, Yaci Store uses a relational database to store data and each store provides a default implementation of corresponding storage api using JPA.

This can be overridden by developers by implementing the storage api in consuming applications.


**3. Controller:** A controller provides REST endpoints to retrieve store's data. It uses the storage api to retrieve data from the persistent store. 
Out of the box, Yaci Store provides some common REST endpoints for all stores. But, developers can implement their own REST endpoints.

<a id="handling-rollbacks"></a>
## Handling Rollbacks

Yaci Store follows a simply strategy to handle rollbacks. As ``store`` modules don't handle any data aggregation, the rollback handling becomes simple.
Each table in a store maintains a ``slot`` column. When a processor received a ``RollbackEvent`` event, it deletes all the records with a slot greater than
the rollback slot mentioned in the event. 

But as aggregates handle data aggregation, the rollback handling becomes a bit complex. But with a simple strategy, we can handle rollbacks in aggregates as well.
**For example,** the ``Account`` module aggregates account balance. But instead of storing the account balance for an address in one record, balance snapshot is stored for a slot.
As we don't update existing records during aggregation, rollback can be handled by simply deleting all the records with a slot greater than the rollback slot mentioned in the event.

If you are using Yaci Store as a library in your application with your own domain model and tables, you can follow the similar strategy to handle rollbacks.

<a id="idempotency"></a>
## Idempotency

Though Yaci Store ensures that the same event is not published more than once, it is possible that the same event is published more than once in some scenarios. All out-of-box 
modules are idepotent. But if you are writing your own stores, you should make sure that your modules are idempotent.
