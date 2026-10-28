# ደላላ ነኝ (Delala Negn) - Ethiopian Real Estate & Broker App 🇪🇹

**ደላላ ነኝ** ገዢዎችን፣ ሻጮችን፣ ተከራዮችን እና አከራዮችን በቀጥታ የሚያገናኝ፣ ከመስመር ውጭ እና ኦንላይን የሚሰራ ዘመናዊ የኢትዮጵያ የደላላ የሞባይል መተግበሪያ ነው።

---

## 🌟 ቁልፍ አገልግሎቶች (Key Features)

1. **የሻጭ፣ አከራይ፣ ገዢና ተከራይ መለያ (Role-based Profiles):**
   - ተጠቃሚዎች እንደ አከራይ/ሻጭ ወይም እንደ ተከራይ/ገዢ ሆነው በመመዝገብ ንብረታቸውን ማስተዳደር ይችላሉ።
2. **የንብረት ዝርዝርና ፍለጋ (Property Listings & Search):**
   - ቤቶች፣ መኪኖች፣ መሬት እና የንግድ ቦታዎች በፎቶ፣ በዋጋ፣ በከተማ እና በአድራሻ ተደራጅተው ይቀርባሉ።
3. **የካርታ እይታ (Map & Location Discovery):**
   - የንብረቶች መገኛ ቦታ በካርታ (Map View) እና በGoogle Maps መክፈቻ አማራጭ።
4. **የይዞታ ማረጋገጫ (Title Deed Verification):**
   - የካርታ ቁጥር፣ የሰነድ ሁኔታ እና የባለቤትነት ማረጋገጫ መረጃዎችን በማየት ከአጭበርባሪዎች መጠበቅ።
5. **የዋጋ ተመን አስሊ (Fair Valuation Calculator):**
   - በአካባቢው እና በወቅቱ ገበያ መሰረት ግምታዊ ትክክለኛ የኪራይ ወይም የሽያጭ ዋጋ ማስያዣ።
6. **የጽሑፍ እና የድምጽ መልእክት (Chat & Voice Messaging):**
   - በሻጭና በገዢ መካከል ቀጥተኛ የጽሁፍ እና የድምጽ መልእክት መለዋወጫ።
7. **አስተማማኝ የአደራ ክፍያ (Escrow Payment Simulation):**
   - በቴሌብር (Telebirr) እና በኢትዮጵያ ንግድ ባንክ (CBE Birr) የታገዘ የውልና የክፍያ አስተማማኝ ስርዓት።
8. **ከመስመር ውጭ መስራት (Offline-first with Room Database):**
   - ኢንተርኔት በሌለበት ጊዜ የተመዘገቡ መረጃዎችን መመልከትና ኢንተርኔት ሲመጣ በቀጥታ ማመሳከር።
9. **የግፊት ማስታወቂያ (Push Notifications with FCM):**
   - አዳዲስ መልእክቶች፣ የኪራይ ጥያቄዎች እና የንብረት ፍላጎቶች ሲኖሩ ፈጣን ማስታወቂያ ማድረስ።

---

## 📲 አፑን ወደ አንድሮይድ ስልክ (Android Phone) ለመጫን (Installation Guide)

አፕሊኬሽኑን በማንኛውም የአንድሮይድ ስልክ (Android 7.0 እና ከዚያ በላይ) ላይ ለመጫን የሚከተሉትን ቀላል ደረጃዎች ይከተሉ፡

### አማራጭ 1፡ ከ AI Studio በቀጥታ APK ማውረድ (Direct APK Export)
1. በ AI Studio የላይኛው ቀኝ ማዕዘን የሚገኘውን **Settings (⚙️)** ወይም **Export** ቁልፍ ይጫኑ።
2. **Download APK** ወይም **Build APK** የሚለውን ይምረጡ።
3. የወረደውን `.apk` ፋይል ወደ ስልክዎ ያስተላልፉ (በUSB ኬብል፣ በቴሌግራም፣ ወይም በGoogle Drive)።

### አማራጭ 2፡ በኮምፒውተር ላይ Build አድርጎ ማውጣት (Build with Gradle)
ተርሚናል ወይም Android Studio ላይ የሚከተለውን ትዕዛዝ ያሂዱ፡
```bash
gradle assembleDebug
```
የተዘጋጀው APK እዚህ ይገኛል፡
`app/build/outputs/apk/debug/app-debug.apk`

### በስልክ ላይ የመጫን ቅደም ተከተል (Installing on the Phone):
1. በስልክዎ ፋይል ማኔጀር ውስጥ የገባውን **app-debug.apk** ፋይል ይጫኑ።
2. ስልክዎ *“Install from unknown sources”* (ያልታወቁ መተግበሪያዎችን መጫን ፍቀድ) የሚል ጥያቄ ሲያቀርብ **Allow** ወይም **Settings > Install Unknown Apps > Allow** የሚለውን ያብሩ።
3. **Install** የሚለውን በመጫን ጭነቱን ያጠናቁ።
4. **Open** ብለው አፑን መጠቀም ይጀምሩ!

---

## 🚀 ወደ GitHub ለመጫን (How to Push to GitHub)

### አማራጭ 1፡ ከ AI Studio UI በቀጥታ (ቀላሉ መንገድ)
1. በ AI Studio የላይኛው ክፍል ወይም የጎን ሜኑ ውስጥ **Push to GitHub** ወይም **Export to GitHub** የሚለውን ይጫኑ።
2. የ GitHub አካውንትዎን ያገናኙ (Authorize)።
3. አዲስ Repository ስም (ለምሳሌ `delala-negn-android`) በመስጠት **Push** ያድርጉ።

### አማራጭ 2፡ በ Git Command Line
```bash
git init
git add .
git commit -m "Initial commit: Delala Negn Ethiopian Real Estate & Rental App"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/delala-negn-android.git
git push -u origin main
```

---

## 🛠 የቴክኖሎጂ ዝርዝር (Tech Stack)

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **Local Persistence:** Room Database (Offline sync)
- **Image Loading:** Coil Compose
- **Notifications:** Firebase Cloud Messaging (FCM)
- **Audio:** Android MediaRecorder / MediaPlayer APIs
- **Architecture:** Clean MVVM (StateFlow, Coroutines, Repository pattern)

---

## 📄 ፍቃድ (License)
ይህ ፕሮጀክት ለሕዝብ ጥቅም ክፍት የተደረገ የኢትዮጵያ ንብረት አገናኝ መተግበሪያ ነው።
