# BotManager
Bot Manager is a program that manages runescape bots.

The botting system consists of 2 parts. The server which manages bot clients, and the scripting used to control the bots in game.
This repository is a snapshot of the Bot Manager part of the project, the rest remains private.

Bot Lifecycle
1. Manually create new runescape account, run it through the tutorial and then add the account credentials into the database.
2. Credentials get saved as a fresh account and a random start date is generated.
3. On start date, account gets converted into a Worker, or a Mule(an account that workers offload wealth onto). Workers get assigned a job(a specific in game task, ie tree cutter, or potion maker.)
4. On shift start Bot Manager launches a new runesacpe client with command line arguements containing all bot information(credentials, jobs, roles, etc..)
5. When the client loads, and script is started, the bot forms a socket connection with the Bot Manager. The Manager assigns a thread to the socket and periodically checks run conditions.
6. If the account fails to login due to being banned it sends a ban notification to the manager and shuts down. The manager then sets the start date for 4 days in the future to account for simple temporary bans. If still banned after 4 days the account gets converted to a banned account and is retired.
8. On login, the bot calls the Task Builder for its assigned job. Builder evaluates the completion of each task(ie get level 5 fishing, or complete xyz quest) and adds the uncompleted tasks onto the task stack.
9. Once bot completes all pre-requisite tasks it begins it's primary task(usually making money) and does that task until told to stop.
10. When told to stop a bot sends a Mule Request to the Manager so it can offload its wealth.
11. Bot Manager launches the mule assigned to that worker and when the mule is loaded, the Manager sends the Mule Target(opposite player name, trade location, and list of items to give the other account) to each bot.
12. Bots receive Mule Target and a Mule Task gets pushed onto the the stack.
13. They meet up, and complete the trade. The Mule Account sends a report to the server with details. Then both pop the Mule task off the stack.
14. Bot then sends out a session profit/loss report, logs out, and destroys its client.
15. Manager does some cleanup and verifies everything shut down correctly before remove this account from the running bots list and setting its start time for tomorrow.
