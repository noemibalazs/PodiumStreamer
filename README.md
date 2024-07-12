This is a Kotlin Multiplatform project targeting Android, iOS.

First steps:

1. Use or got your personal Mastodon access token
2. Add your personal access token to the util / Utils / ACCESS_TOKEN

Idea:

1. Use Ktor to handle the Server-Sent Events based on a searched term
2. Storing all events in local data base using Room
3. Use Koin for dependency injection
4. Monitoring the network connection to prevent the local data base to be cleared
5. Clicking on an event will display the event details inside of a dialog

Libraries:

1. Koin DI 
2. Jetpack Compose 
3. Coroutines 
4. Flow 
5. Room 
6. Ktor 
7. MVVM architecture 
8. Material Design 3