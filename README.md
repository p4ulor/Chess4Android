# PDM-2122i-LI5X-G19
Chess games app Mobile Device Programming discipline. 
You can:
- Play chess offline
- Play online with other users. Create a challenge/lobby with, wait for someone to accept.
- Solve daily chess puzzles obtained from the [lichess API](https://lichess.org/api/puzzle/daily). You can try these puzzles on the browser [here](https://lichess.org/training/daily). These puzzles get saved on the app, along with their states of completion and meta-data, like the ID and date.

## [Download the app here](https://drive.google.com/file/d/1TtapvHg-tSxlts0qBd9GkK5eshmzi1Bh/view?usp=sharing)

## Shortcuts for main source code
- [src](Chess4Android/app/src/main/java/pt/isel/pdm/chess4android)
- [layout (activities)](Chess4Android/app/src/main/res/layout)

## Repository contents
- **_imgs** -> Images used in this README file or other images with technical details
- **AppDocumentation** -> Images, diagrams and texts that explain the app on a programmer level and user level
- **Chess4Android** -> The android project. Open using Android Studio
- The 3 .pdf's named after PDM in this directory are the 3 assignment papers that outline the goals and work to be done in each one. Project presentation.pdf is a powerpoint presentation
- this README file, explains what this entire repository is about, a thing most people don't do for some reason
- src_code shortcut, a directory independent shortcut to the source code, opens it in another explorer window

## Activity/Screens demonstration
![](AppDocumentation/AppDemo_arrows.png)

## Fundamental libraries and technologies used
- [Kotlin serialization](https://github.com/Kotlin/kotlinx.serialization). Used for turning the json string data obtained from the lichess API and convert it to an object
- [Android Volley](https://developer.android.com/training/volley/simple). Used for making a get request do the lichess API
- [Android Room](https://developer.android.com/training/data-storage/room). Used for storing chess puzzles in the phone's local database
- [Firebase](https://developer.android.com/studio/write/firebase?hl=en). Used for playing online. To create games, accept games and pass data between the user's moves and player turns.
- [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel?hl=en). Used for saving data from an activity when the activity rotates or is when it's running in the background
- [LiveData](https://developer.android.com/topic/libraries/architecture/livedata?hl=en). Used for observing values in the activitie's ViewModel and notifying them to the activity


## This is how proud I am for doing this project. ALL DONE BY ME
![](https://c.tenor.com/O5jZtKfuDRoAAAAd/penguinz0-yeah-baby.gif)

Special thanks to:
- My teacher, [Paulo Pereira](https://github.com/palbp), for his teaching skills, good humor and personality and help
- Joe Rogan for keeping me entertained while listening to his podcasts, which avoided me from crashing my dopamine while doing this project. And thus, keeping me focuses
- Three 6 Mafia for getting me through the some stress I go through sometimes and for giving me resilience, hype me up, make me man up and put in work

