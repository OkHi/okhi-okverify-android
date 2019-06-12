

The OkHi android SDK is a seamless way to collect and use accurate customer location information. 

The SDK is a customizable UI within your app that enables users to create or lookup existing OkHi locations.

An OkHi location consists of a high accurate GPS pin, a photo of gate and a weblink that enables either to navigate to the location or to plot your user demographics on the OkHi Insights web-app.

OkHi SDK gives your app the ability to collect an address for a user as well as update the user’s address.

The minimum supported SDK version is 19 (android 4.4)


Steps for integration

1. Add the maven repository into your project build.gradle
	
maven { url 'https://jitpack.io' }


2. In your app module build.gradle dependencies, add the following dependencies and sync gradle files

implementation 'com.github.OkHi:okhi-android-sdk:1.3.0'


For apps that are using gradle version 2.x.x and below use compile instead of implementation.

compile  'com.github.OkHi:okhi-android-sdk:1.3.0'



3. Initialize okhi sdk by adding the following line in your application class

public class YourCustomApplication extends Application {
@Override
Public void onCreate(){
super.onCreate()
OkHi.initialize("Your API key");

}
}
Use this api key for testing “r:b59a93ba7d80a95d89dff8e4c52e259a”

When you have finished testing and you want to go live, request for a production key here

4. Customize the OkHi appearance in the app

You can customize the sdk by adding the following line  in your application class

OkHi.customize("header background color", "organisation name", "logo url");

color is in rgb format
Name of your organization
Url link of the company logo

Example as below

OkHi.customize("rgb(0, 131, 143)", "okhi", "https://lh3.ggpht.com/GE2EnJs1M1Al9_Ol2Q1AV0VdSsvjR2dsVWO_2ARuaGVS-CJUhJGbEt_OMHlvR2b8zg=s180");

5. Launch the library as follows

	Start the library

JSONObject jsonObject = new JSONObject();
jsonObject.put("firstName", "firstName");
jsonObject.put("lastName", "lastName");
jsonObject.put("phone", "phone");
OkHi.displayClient(okHeartCallback, jsonObject);

Example as below

JSONObject jsonObject = new JSONObject();
jsonObject.put("firstName", "Ramogi");
jsonObject.put("lastName", "Ochola");
jsonObject.put("phone", "0713567907");
OkHi.displayClient(okHeartCallback, jsonObject);


6.  Create a callback to receive the responses from the library

	OkHiCallback okHiCallback = new OkHiCallback() {
   		@Override
   		public void querycomplete(JSONObject jsonObject) {
       		//The callback will return the jsonobject once its done.
   		}
};



7. Return objects

When the user goes through the address creation process successfully, the following json object is returned in the callback.

Success
{
   "location": {
       "streetName": "Yellow brick road",
       "lat": -1.2343261,
       "lng": 36.6642569,
       "placeId": "1A8sxd6V9W",
       "propertyName": "Mitte lane court",
       "directions": "turn is gud",
       "id": "dEwmYjtpqc",
       "url": "https://receive2.okhi.co/dEwmYjtpqc",
       "otherInformation": "Please pet the dog when you arrive"
   },
   "user": {
       "phone": "+254713567907",
       "firstName": "Ramogi",
       "lastName": "Ochola"
   }
}
Returned after the user has successfully completed address creation.

{
  "message": "non_fatal_exit",
  "payload": {
    "Response": "Address creation completed successfully"
  }
}
Returned after the user has successfully completed address creation.

When the user does not go through the address creation process successfully, the following json object is returned in the callback.

{
  "message": "fatal_exit",
  "payload": {
    "Error": "Network error"
  }
}
Returned when there is no internet connection.

{
  "message": "fatal_exit",
  "payload": {
    "Error": "Address creation did not complete"
  }
}
Returned if the user did not complete the address creation

{
  "message": "fatal_exit",
  "payload": {
    "Error": "phone is required in user object"
  }
}
Returned if a parameter is missing


