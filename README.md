#How to use "Geo-tagged_time_capture"?

##Login and Register 
 
 
####Users can register with username,email,password and upload one profile photo. 
####These dates will be transmitted to the Mysql server through the PHP. 
###Then you can login with your email and password. If you don't change your password or logout, next time you will login automatically.
####On the top-right menu you can choose to logout.
####After login, the username,email,profile photo will be shown in the nav_header of the navigationView.
 ---
 
##In the NavigationView you can choose to do follows:
 ---
  

##Take the photo with our cameras



####In this part your have 2 choices, system camera or my compass camera. 
####The left FloatingActionbutton is the compass camera, the other one is the system camera. 
--- 

##Compass camera(To use it,your mobile phone must be rooted first)
 
 
#### In the compass camera, on the top-left you can find three parameters of the compass:Azimuth,Pitch and Roll. 
####In the center is the transparent compass,under which it's the camera preview.
 ---

##Import your photo into SQLite

 
#### After taking the photo with the camera, you will see a 100*100 sized photo,and the information(time,coordinates,address) under it. 
###If you can't get your location with GPS, please open the mobile phone's GPS and go outside of the building to retry it.
---

####Once you get the Location, you can save your photo to the mobile phone, and all its dates would be stored in the SQLite database.
### For instance, they will be used in the Local-filter part.

---

##Get you Location
 
 
####Simple part, just help you to take your location directly.
###Every 10 seconds you can try one time. Remember that don't use it continuously, it may lead the ANR(Application not response). 
####Use it when you really need it(Open the GPS,and stay outside!).
---

##Local-filter

 
####This filter will help you to filter your local images with location.
### It could be used only when you get your location. In addition, once you get the location, it will show all the photos at this location.
### The photos are in chronological order, and you can use button "BACK" or "NEXT" to look at previous  photo or next photo.
---
##Compass
 
 
####Simple compass application.
---
##Share
 
 
####This part allows you to take photos like 'Import' part,but it will upload your photos to the server. 
###All the related information, like coordinates,uploader,time,address will be stored in the Mysql.
---
##Network-filter

 
####Now this is the network filter.
###You can look at all the people uploaded photo at this place. What's more than Local-filter, you can look at who uploaded this photo.

 
 
