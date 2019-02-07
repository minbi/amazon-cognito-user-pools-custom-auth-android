# Custom Auth via Amazon Cognito User Pools

This sample was setup to work with the instructions found here: https://docs.aws.amazon.com/cognito/latest/developerguide/user-pool-lambda-challenge.html

This sample has very minimal UI because it's not the point of this sample.

## Requirements

* [AndroidStudio 3.2+](https://developer.android.com/studio/)
* Android API 15+
* [Amplify CLI 0.1.45+](https://aws-amplify.github.io/docs/)

## Using the Sample

1. Import the CustomAuth project into Android Studio.
    - From the Welcome screen, click on "Import project".
    - Browse to the CustomAuth directory and press OK.
    - Accept the messages about adding Gradle to the project.
    - If the SDK reports some missing Android SDK packages (like Build Tools or the Android API package), follow the instructions to install them.

1. Replace the values found here with your values
    ```
    {
      "CognitoUserPool": {
        "Default": {
          "PoolId": "us-east-1_XXXXXXXXXXX",
          "AppClientId": "XXXXXXXXXXXXXXXXXXXXXXXXXXXX",
          "AppClientSecret": "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
          "Region": "us-east-1"
        }
      }
    }
    ```

1. Replace the USERNAME and PASSWORD found in `MainActivity.java`