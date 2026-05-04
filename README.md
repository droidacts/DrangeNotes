#  D[riving]-range-notes
## Logger app for golf shots
- May 2026: Maintenance release
    - Rebuild with android studio Panda4, compile and target SDK set to 36. 
    - Updated kotlin version and some androidx lib version.

- Jul 2025:
Rebuilt using android studio Ladybug (2024.2.2.15) targeting SDK 35 as required.
We have tested the app on a current android 14 phone.

- Jan 2024:
The project is now configured for android studio hedgehog with build tools SDK 34.
The app has been tested only on old android phones running 7.1.1 & 9.0.
If you update the initial DB file in assets, an app uninstall + phone reboot may be needed.

### Summary
The name of the app is "Drange Logs". It is made to help with logging shots while practising in a driving range facility. Typical uses are:
- in lieu of pen and paper, record the shots in one of the two user interfaces suiting different preferences;
- export (share) the current entries using _logs_ to Google Keep, Gmail, or similar;
- under options menu, export (backup) the app database (with all entries) to a shared external storage (local SD card); then use an external sqlite tool to edit the logs.
