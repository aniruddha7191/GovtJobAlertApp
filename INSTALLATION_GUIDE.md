# Govt Job Alert - Installation & Build Guide

Follow these steps carefully to connect Firebase and generate your installable APK file completely free and without any coding!

## Step 1: Set Up Firebase (Mandatory)
Since this app uses a real-time database and push notifications, you need to connect it to your own Google Firebase account.

1. Go to the [Firebase Console](https://console.firebase.google.com/) and log in with your Google account.
2. Click **Add Project** and name it "Govt Job Alert".
3. Disable Google Analytics (optional, to keep it simple) and click **Create Project**.
4. Once the project is ready, click on the **Android icon** (white robot) on the project dashboard to add an Android app.
5. In the **Android package name** field, enter exactly: `com.govtjobalert`
6. Click **Register App**.
7. Click the **Download google-services.json** button. **(CRUCIAL STEP)**
8. Place the downloaded `google-services.json` file directly inside the `app` folder of this project folder on your PC (`Govt Job Alert/app/google-services.json`).
9. Click Next and skip the remaining steps until you are back at the console.

### Enable Firestore Database
1. In the left menu of the Firebase Console, go to **Build > Firestore Database**.
2. Click **Create Database**.
3. Choose **Start in test mode** (this allows you to add jobs immediately without complex security rules) and complete the setup.

---

## Step 2: Build the APK using GitHub (No Android Studio Needed!)
We have included a completely automated system to build your APK using GitHub's servers.

1. Create a free account on [GitHub](https://github.com/).
2. Create a new, empty repository (name it something like `GovtJobAlertApp`). Make it Private or Public.
3. Upload all the files from your `Govt Job Alert` folder (including the `google-services.json` you downloaded) directly into the new GitHub repository. You can just drag and drop them on the GitHub website or use Git.
4. Once the files are uploaded, click on the **Actions** tab at the top of your GitHub repository.
5. You will see a workflow named **Build Android APK**. Click on it.
6. Click the **Run workflow** dropdown button on the right side, and click the green **Run workflow** button.
7. Wait about 3-5 minutes. GitHub's servers are now downloading the Android SDK and compiling your code!
8. When it finishes successfully (green checkmark), click on the completed run.
9. Scroll down to the **Artifacts** section at the bottom of the page.
10. Click on **GovtJobAlert-APK** to download the compiled `.zip` file.
11. Extract the `.zip` file, and inside you will find `app-debug.apk`.

---

## Step 3: Install the App
1. Transfer the `app-debug.apk` file to your Android phone (via USB, email, WhatsApp, or Google Drive).
2. Open the file on your phone to install it.
3. (If your phone warns you about installing apps from unknown sources, allow it in settings).
4. Open the app! 

**Admin Access:**
To add jobs, go to the **Admin** tab at the bottom of the app.
- **Username:** `admin`
- **Password:** `admin123`
