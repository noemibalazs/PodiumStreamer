import SwiftUI
import composeApp

@main
struct iOSApp: App {

     init() {
         KoinHelperKt.doInitKoin()
        }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
