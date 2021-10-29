# StreamWatcher
This project is a server that watches a livestream for a specific image. Once the image is detected, a notification is sent to its clients. 

The image detection in this project simply watches a specific area of the screen for pixel values. 
Once a predetermined threshold is met the thread watching the stream can inform the webservers clients that the image is most likely there. 
I used a simple aglorithm for the image detection because more robust/complex solutions where too slow and would begin to lag behind the stream.

## [Example](https://www.youtube.com/watch?v=MDvIGO-pR2U)
This is an example where an android application displays a notification to a user after an image appears on screen. 
This application connects to the server and waits for the signal to display the notification. 


In this particular example the livestreamer displays an image on the screen, this is to encourage viewer engagement by having 
watchers interact in the chat. The user of the application can click the collect button to automatically send the 
message the streamer wants the chat to spam. 
