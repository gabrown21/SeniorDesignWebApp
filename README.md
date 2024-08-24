# StockApp

A high level overview of the StockApp application is below:

<img width="1535" alt="image" src="https://github.com/user-attachments/assets/45591086-3886-428c-9279-5fecf95a3611">

It consists of the following components:


## WebSocket Listener - Finhub API

The data input to StockApp is the Finhub API.  More specifically, StockApp supports [WebSocket](https://ably.com/topic/websockets) subscriptions for price & trade volume updates for a set of stock symbols.  These updates are the core source of data for StockApp.

## Analytics Computor

The responsibility of the analytics computor is to take the data that StockApp has received from Finhub and based on this data expose meaningful, actionable information back to StockApps's end users.  For example, the analytics computor is responsible for computing the numeric responses to questions like:

* what is the current price of <symbol>?
* of the symbols being tracked, which one has experienced the highest price fluctuation over the last 3 hours?

## In-memory data store

StockApp will receive data from Finhub continuously.  StockApp's end users will ask questions about the data at unpredictable intervals.  Some representation of the data received from Finhub must be stores somewhere to facilitate this, and this is the current implementation of that storage.

## Git & GitHub Resources

### Git
- [Git site](https://git-scm.com/)
- [Git tutorial](https://git-scm.com/docs/gittutorial)
- [Git for computer Scientists](https://eagain.net/articles/git-for-computer-scientists/)

### GitHub

### GUI Clients
- [GitHub Desktop](https://github.com/apps/desktop)
- [SourceTree](https://www.sourcetreeapp.com/)