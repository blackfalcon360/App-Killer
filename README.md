# App Killer

Android app (Java, no AndroidX, no libraries) to stop other apps and, where Android allows it, freeze them.
Package: `com.blackfalcon.appkiller`. By: Black Falcon

## What it does
- Lists installed apps (search, optional system apps, select many).
- **Kill background**: asks Android to stop the background processes of the selected apps (`killBackgroundProcesses`).
- **Force stop**:
  - With root: runs `am force-stop` for each app.
  - Without root: an Accessibility service opens each app's App Info screen and presses "Force stop" and the confirm button for you.
- **Freeze / Unfreeze**: with root (`pm disable-user` / `pm enable`). Without root the app shows ready-to-copy ADB commands instead.
- Protected apps (launcher, Settings, System UI, phone, Google Play services) are skipped for force stop and freeze.
- Languages: English (default), Urdu, Arabic. Light and dark theme (default dark). Settings tab with Save.

## Build
1. Upload this folder's contents to a GitHub repository.
2. GitHub Actions ("Build APK") runs on every push. Download the APK from the run's Artifacts (`AppKiller-APK`).

## Setup for Force stop without root
1. Install the APK. On Android 13+ open the app's App Info, tap the three-dot menu, choose "Allow restricted settings".
2. Settings > Accessibility > App Killer > turn on.
3. Select apps, tap Force stop, and do not touch the screen until it finishes.

## Privacy
No internet permission. Nothing leaves your phone. The Accessibility service only acts on the App Info screen while Force stop automation is running.

## Limitations (honest)
- Android does not let a normal app force-stop or freeze other apps directly. Only root, ADB or the system can.
- Android does not tell normal apps which apps are running. "Not stopped" only means the app has not been force-stopped.
- Kill background cannot stop apps with a foreground service; many apps restart themselves.
- Accessibility force stop depends on your phone's Settings app (button text/layout). It was written for stock Android and common wording; it may skip apps on some phones or languages. Android may also block opening the next screen from the background; the final message shows how many were done and skipped.
- Freeze needs root or ADB. Freezing system apps can break your phone.
- Xiaomi/Oppo/Vivo/Huawei: allow Autostart and disable battery restriction for this app if the Accessibility service gets turned off.
- Not compiled or tested on a device by the author; build errors, if any, should be reported with a screenshot.

## اردو (مختصر)
- یہ ایپ منتخب ایپس کو بیک گراؤنڈ میں بند کرتی ہے، فورس اسٹاپ کرتی ہے، اور فریز کرتی ہے (صرف روٹ یا ADB سے)۔
- روٹ کے بغیر فورس اسٹاپ ایکسیسبیلٹی سروس سے ہوتا ہے جو ایپ انفو اسکرین کا بٹن آپ کے لیے دباتی ہے۔
- اینڈرائیڈ 13+ پر پہلے ایپ انفو سے "Allow restricted settings" دینا پڑ سکتا ہے۔
- انٹرنیٹ کی اجازت نہیں؛ کچھ بھی فون سے باہر نہیں جاتا۔
- حدود: عام ایپ دوسری ایپس کو براہ راست فریز نہیں کر سکتی؛ چلنے والی ایپس کی فہرست اینڈرائیڈ نہیں دیتا؛ کچھ فونز پر آٹومیشن کام نہ کرے۔
