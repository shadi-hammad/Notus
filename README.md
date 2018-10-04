# Notus
Notus Weather App.

<a href="https://github.com/shadi-hammad/Notus"><img src="https://i.imgur.com/Wy4mklb.png?1" width="500" height="500"></a>      <a href="https://play.google.com/store/apps/details?id=com.shadihammad.stormy"><img src="https://i.imgur.com/rvD32OV.png" width="350" height="150"></a>




Notus is a weather app designed to retrieve accurate, up-to-the-minute weather data for your location. Notus uses the Dark Sky Forecast API to access weather data from around the world. The app uses GPS services to quickly - and accurately - obtain the user's location. Notus features a straight-forward, simple design to quickly display both current and future weather conditions to the user. Thanks to Places Search from Google Places API for Android, Notus now allows users to search from thousands of cities and receive instantaneous weather information! 

Notus also uses a dynamic background color scheme that automatically matches current weather conditions (e.g. sunny conditions will result in an orange-colored background) to give the app a stylish and in-tune feel.


## Getting Started

![Main Activity](https://i.imgur.com/5M9yx0F.png)

Upon opening the application, you will be welcomed a screen similar to the one pictured above. This screen displays the current weather conditions for a particular location. Notus will use location services to fetch your current location (note: Android's location feature must be turned on for this feature to work - Notus will ask the user to enable this feature if it detects that it is disabled). The user can also manually select any city from the location selector screen; this screen can be accessed by tapping on the location selector button on the top-right.

Tapping on the refresh button at the top of the screen will update the activity with the most recent weather data available from https://darksky.net/forecast/.

Tapping on the hourly or 7-day forecast buttons on the bottom of the screen will launch the hourly forecast or 7-day forecast activities, respectively. Below is an example 7-day forecast activity:

<a href=""><img src="https://i.imgur.com/uznUlOI.png" width="300" height="550"></a>     <a href=""><img src="https://i.imgur.com/ZSMxIRR.png" width="300" height="550"></a>

Upon launch, the 7-day forecast activity will display weather information for the upcoming week. Tapping on a particular day will result in a short message popping up at the bottom of the screen: this message will display a brief description of the overall forecast outlook for the selected day. An example of this can be seen in the screenshot on the right. 
