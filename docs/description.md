# Drange Logs
## Logger app for golf shots

The name of the app is "Drange Logs". 
It is made to help with logging shots while practising in a driving range facility. 
In other words, it is a notebook to record shots.
To improve, golfers (the more serious ones) would need to know how far they typically hit with each club in the bag.
As launch monitors are quite accessible nowadays, we can simple jog down the yardage numbers reported by the machine in a notebook.
Some launch monitors have associated phone apps to record the statistics. We found that can be cumbersome or unreliable to use and lacking common standards.
So, we may as well record our data in our phones in a plain format. 
In fact, there are several Toptracer equipped driving ranges, indoor simulators, and even a Topgolf venue nearby.
I may go to those places to practise and log all my shots in my app,
in the hopes that the data may prove useful afterwards (post-processing is needed).

## Usage
There are two user interfaces to choose from by swiping the screen left-or-right. 
The basic UI is a fill form and drop-downs to select the relevant info and enter the yardage
as read from the launch monitor. 
The round button in the bottom will log the shot into the app SQL database.
The shot shape buttons provide optional info.
The notes style UI mimic a notebook page. 
Tap a cell to enter the yardage for the corresponding club and swing, then hit the bottom round button to log the shot.

From the options menu, one can get to the logs interface to query the 
database for recent shots entries.
The bottom share button in the logs interface can export the current displayed query results through various apps 
such as Gmail, Gmail Chat, Google Keep Notes, SMS, etc.
Typically after each practice I would export the session shots to Google Keep Notes so that I can examine the data closely.
And from time to time I would export the whole app database (select from options menu) to external SD card for backup purposes.

Conceptually, using standard SQL techniques, one can compute yardages, spreads, and tendencies from the database.
For now, that has to be done outside of the app, with tools such as DB Browser for SQLite.

## Afterthoughts
While this app is mainly tailored for my needs, hopefully useful to some folks somewhere,
it is also made for educational purposes.
In particular,
this app also serve as demonstration codes for the (perhaps still preferred) android MVVM architecture with room database and fragment navigation inside a single activity.
Communications between fragments (the UIs) are mostly just shared activity-scoped viewmodels.
Database operations are done with coroutines. 
We also demonstrated how to write a file to shared external storage without explicit permission requests.

